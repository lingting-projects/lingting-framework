package live.lingting.framework.thread

import live.lingting.framework.concurrent.Await
import live.lingting.framework.function.StateKeepRunnable
import live.lingting.framework.function.ThrowableRunnable
import live.lingting.framework.lock.JavaReentrantLock
import live.lingting.framework.thread.platform.PlatformThread
import live.lingting.framework.thread.virtual.VirtualThread
import java.time.Duration
import java.util.concurrent.BlockingQueue
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executor
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeoutException

/**
 * @author lingting 2023-06-05 17:31
 */
open class Async @JvmOverloads constructor(
    /**
     * 异步任务使用的线程池
     */
    protected val executor: Executor = defaultExecutor,
    /**
     * 线程数量限制. -1 表示不限制
     */
    val limit: Long = UNLIMITED
) {

    companion object {

        @JvmStatic
        var defaultExecutor: Executor = VirtualThread

        const val UNLIMITED: Long = -1

        @JvmStatic
        @JvmOverloads
        fun platform(limit: Long = UNLIMITED): Async {
            return Async(PlatformThread, limit)
        }

        @JvmStatic
        @JvmOverloads
        fun virtual(limit: Long = UNLIMITED): Async {
            return Async(VirtualThread, limit)
        }

    }

    protected val lock = JavaReentrantLock()

    /**
     * 所有异步任务列表
     */
    protected val all = CopyOnWriteArrayList<StateKeepRunnable>()

    /**
     * 执行中任务列表
     */
    protected val running = CopyOnWriteArrayList<StateKeepRunnable>()

    /**
     * 已完成异步任务列表
     */
    protected val completed = CopyOnWriteArrayList<StateKeepRunnable>()

    /**
     * 待执行任务队列
     */
    protected val queue: BlockingQueue<StateKeepRunnable> = LinkedBlockingQueue()

    constructor(limit: Long) : this(defaultExecutor, limit)

    /**
     * 是否可以无限制使用线程
     */
    val isUnlimited: Boolean
        get() = limit == UNLIMITED

    /**
     * 是否已满, 不能立即执行新任务
     */
    val isFull: Boolean
        get() = !isUnlimited && running.size >= limit

    fun execute(runnable: Runnable) {
        execute("", runnable)
    }

    fun execute(name: String?, runnable: Runnable) {
        submit(name) { runnable.run() }
    }

    fun submit(runnable: ThrowableRunnable) {
        submit("", runnable)
    }

    fun submit(name: String?, runnable: ThrowableRunnable) {
        val keepRunnable: StateKeepRunnable = object : StateKeepRunnable(name) {

            override fun doProcess() {
                runnable.run()
            }

            override fun onEnd() {
                lock.run {
                    completed.add(this)
                    running.remove(this)
                    walk()
                }
            }
        }

        all.add(keepRunnable)
        queue.add(keepRunnable)
        walk()
    }

    /**
     * 唤醒所有任务. 尝试执行
     */
    fun walk() {
        // 上锁确保不会多执行
        lock.runByTry {
            // 已满
            if (isFull) {
                return@runByTry
            }
            val runnable = queue.poll() ?: return@runByTry
            executor.execute(runnable)
            running.add(runnable)
        }
    }

    /**
     * 等待结束
     * @param duration       超时时间
     * @param forceInterrupt 是否强制中断已超时的任务
     */
    @JvmOverloads
    fun await(duration: Duration? = null, forceInterrupt: Boolean = true) {
        try {
            Await.waitTrue(duration) { notCompletedCount() < 1 }
        } catch (e: TimeoutException) {
            if (forceInterrupt) {
                interruptAll()
            }
            throw e
        }
    }

    /**
     * 等待可线程空闲, 此时可立即执行新任务
     */
    fun awaitIdle() = Await.waitFalse { isFull }

    /**
     * 中断所有运行中任务
     */
    fun interruptAll() {
        for (runnable in running) {
            runnable.interrupt()
        }
    }

    /**
     * 执行中和待执行的任务数量
     */
    fun notCompletedCount(): Long {
        return ((queue.size + running.size).toLong())
    }

    fun runningCount(): Long {
        return running.size.toLong()
    }

    /**
     * 已完成的数量
     */
    fun completedCount(): Long {
        return completed.size.toLong()
    }

    /**
     * 所有异步任务数量
     */
    fun allCount(): Long {
        return all.size.toLong()
    }

    fun clearCompleted() {
        val list = completed.toList()
        all.removeAll(list)
        completed.removeAll(list)
    }

}
