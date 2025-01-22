package live.lingting.framework.thread

import java.util.concurrent.Callable
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.atomic.AtomicLong
import java.util.function.Supplier
import live.lingting.framework.function.KeepRunnable
import live.lingting.framework.function.ThrowableRunnable
import live.lingting.framework.thread.executor.PolicyThreadPoolExecutor
import live.lingting.framework.thread.executor.PolicyThreadPoolProperties
import live.lingting.framework.thread.executor.ThreadPoolExecutorServiceImpl

/**
 * @author lingting 2022/11/17 20:15
 */
object ThreadPool {

    @JvmStatic
    val instance by lazy { ThreadPoolExecutorServiceImpl(newExecutor()) }

    @JvmStatic
    fun newExecutor(): ThreadPoolExecutor {
        val atomic = AtomicLong()
        return newExecutor { runnable -> Thread(null, runnable, "t-${atomic.incrementAndGet()}") }
    }

    @JvmStatic
    fun newExecutor(factory: ThreadFactory): ThreadPoolExecutor {
        val properties = PolicyThreadPoolProperties.create(factory)
        return PolicyThreadPoolExecutor(properties)
    }

    @JvmStatic
    fun executor(): ThreadPoolExecutor {
        return instance.executor()
    }

    @JvmStatic
    @JvmOverloads
    fun update(executor: ThreadPoolExecutor, closeOld: Boolean = true): ThreadPoolExecutorServiceImpl {
        val old = instance.executor()
        return instance.executor(executor).also {
            // 替换成功后关闭原线程池
            if (closeOld && old != executor && (old.isShutdown || !old.isTerminated)) {
                old.shutdown()
            }
        }
    }

    @JvmStatic
    val isRunning: Boolean
        /**
         * 线程池是否运行中
         */
        get() = instance.isRunning

    @JvmStatic
    val corePoolSize: Long
        /**
         * 核心线程数
         */
        get() = instance.corePoolSize

    @JvmStatic
    val activeCount: Long
        /**
         * 活跃线程数
         */
        get() = instance.activeCount

    @JvmStatic
    val taskCount: Long
        /**
         * 已执行任务总数
         */
        get() = instance.taskCount

    @JvmStatic
    val maximumPoolSize: Long
        /**
         * 允许的最大线程数量
         */
        get() = instance.maximumPoolSize

    @JvmStatic
    val isReject: Boolean
        /**
         * 是否可能触发拒绝策略, 仅为估算
         */
        get() = instance.isReject

    fun execute(runnable: ThrowableRunnable) {
        instance.execute(runnable)
    }

    fun execute(name: String, runnable: ThrowableRunnable) {
        instance.execute(name, runnable)
    }

    fun execute(runnable: KeepRunnable) {
        instance.execute(runnable)
    }

    fun <T> async(supplier: Supplier<T>): CompletableFuture<T> {
        return instance.async(supplier)
    }

    fun <T> submit(callable: Callable<T>): Future<T> {
        return instance.submit(callable)
    }

}
