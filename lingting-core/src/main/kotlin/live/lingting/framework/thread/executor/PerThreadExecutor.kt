package live.lingting.framework.thread.executor

import live.lingting.framework.function.StateRunnable
import live.lingting.framework.time.StopWatch
import live.lingting.framework.util.TimeUnitUtils.toChronoUnit
import live.lingting.framework.value.WaitValue
import java.time.Duration
import java.util.concurrent.Callable
import java.util.concurrent.CancellationException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.FutureTask
import java.util.concurrent.RejectedExecutionException
import java.util.concurrent.ThreadFactory
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * @author lingting 2025/5/13 13:44
 */
class PerThreadExecutor(val factory: ThreadFactory) : ExecutorService {

    private val stateValue = WaitValue.of(ExecutorState.RUNNING)

    private val lock = stateValue.lock

    val state
        get() = stateValue.value!!

    private val set = ConcurrentHashMap.newKeySet<Thread>()

    val threads
        get() = set.toList()

    private fun tryShutdown(interrupt: Boolean) {
        if (state.isShutdown) {
            return
        }
        lock.runByInterruptibly {
            if (!state.isShutdown) {
                stateValue.value = ExecutorState.SHUTDOWN
                tryTerminate()
            }
            if (interrupt) {
                threads.forEach {
                    if (!it.isInterrupted) {
                        it.interrupt()
                    }
                }
            }
        }
    }

    private fun tryTerminate() {
        if (state.isTerminated) {
            return
        }
        lock.runByInterruptibly {
            check(state.isShutdown) { "terminate must on shutdown after" }
            stateValue.value = ExecutorState.TERMINATED
        }
    }

    override fun shutdown() {
        tryShutdown(false)
    }

    override fun shutdownNow(): List<Runnable> {
        tryShutdown(true)
        return emptyList()
    }

    override fun isShutdown(): Boolean {
        return state.isShutdown
    }

    override fun isTerminated(): Boolean {
        return state.isTerminated
    }

    fun awaitTermination(duration: Duration): Boolean {
        try {
            stateValue.wait(duration) { it?.isTerminated != false }
        } catch (_: TimeoutException) {
            //
        }
        return isTerminated
    }

    override fun awaitTermination(timeout: Long, unit: TimeUnit): Boolean {
        val duration = Duration.of(timeout, unit.toChronoUnit())
        return awaitTermination(duration)
    }

    override fun <T> submit(task: Callable<T>): Future<T> {
        val future = FutureTask(task)
        execute(future)
        return future
    }

    override fun <T> submit(task: Runnable, result: T): Future<T> {
        return submit(Executors.callable(task, result))
    }

    override fun submit(task: Runnable): Future<*> {
        return submit(Executors.callable(task))
    }

    private fun <T> cancelAll(futures: List<Future<T>>, start: Int) {
        for (i in start until futures.size) {
            futures[i].cancel(true)
        }
    }

    override fun <T> invokeAll(tasks: Collection<Callable<T>>): List<Future<T>> {
        return invokeAll(tasks, -1, TimeUnit.NANOSECONDS)
    }

    override fun <T> invokeAll(
        tasks: Collection<Callable<T>>,
        timeout: Long,
        unit: TimeUnit
    ): List<Future<T>> {
        val futures = ArrayList<Future<T>>(tasks.size)
        // 处理到的任务索引
        var index = 0

        try {
            tasks.forEach { t ->
                val f = submit(t)
                futures.add(f)
            }

            while (index < futures.size) {
                val f = futures[index]
                if (!f.isDone) {
                    try {
                        // 等待每个任务结束.
                        if (timeout < 0) {
                            f.get()
                        } else {
                            f[timeout, unit]
                        }
                    } catch (_: ExecutionException) {
                        // 发生异常跳过
                    } catch (_: CancellationException) {
                        //
                    }
                }
                index++
            }

            return futures
        } finally {
            // 当本线程被中止或其他异常时, 停止所有未处理的任务
            cancelAll(futures, index)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> invokeAny(tasks: Collection<Callable<T>>): T & Any {
        return invokeAny(tasks, -1, TimeUnit.NANOSECONDS) as (T & Any)
    }

    @Suppress("kotlin:S3776")
    override fun <T> invokeAny(
        tasks: Collection<Callable<T>>,
        timeout: Long,
        unit: TimeUnit
    ): T? {
        require(tasks.isNotEmpty()) { "tasks is empty!" }
        val duration = if (timeout < 0) null else Duration.of(timeout, unit.toChronoUnit())
        val v = WaitValue.of<T>()
        val r = WaitValue.of<StateRunnable>()
        val ex = WaitValue.of<Throwable>()

        val rs = ArrayList<StateRunnable>(tasks.size)

        try {
            val watch = StopWatch()
            watch.start()
            tasks.forEach { t ->
                val runnable = PerStateRunnableImpl(t, v, r, ex)
                execute(runnable)
                rs.add(runnable)
            }

            while (true) {
                val d = watch.duration()
                if (duration != null && duration <= d) {
                    throw TimeoutException("invoke timeout. expect: $duration. current: $d")
                }

                val runnable = r.value
                // 存在正常跑完的线程
                if (runnable != null) {
                    // 返回该线程的结果
                    return v.value
                }

                // 移除跑完的
                rs.removeIf { it.isFinish }
                // 空了, 全部都是异常结束的
                if (rs.isEmpty()) {
                    // 抛出第一个异常
                    throw ex.value ?: IllegalStateException("all task failed!")
                }
            }
        } finally {
            rs.forEach { it.interrupt() }
        }
    }

    override fun execute(command: Runnable) {
        if (state.isShutdown) {
            throw RejectedExecutionException("executor is $state")
        }
        val t = factory.newThread {
            try {
                command.run()
            } finally {
                set.remove(Thread.currentThread())
            }
        }
        set.add(t)
        try {
            t.start()
        } catch (th: Throwable) {
            set.remove(t)
            throw th
        }
    }

    class PerStateRunnableImpl<T>(
        val t: Callable<T>,
        val v: WaitValue<T>,
        val r: WaitValue<StateRunnable>,
        val ex: WaitValue<Throwable>
    ) : StateRunnable() {

        override fun doProcess() {
            try {
                val call = t.call()
                // 没有值的情况下更新为当前值
                r.compute {
                    if (it == null) {
                        v.update(call)
                        this
                    } else {
                        it
                    }
                }
            } catch (t: Throwable) {
                // 第一个异常更新
                ex.compute { it ?: t }
            }
        }
    }

}
