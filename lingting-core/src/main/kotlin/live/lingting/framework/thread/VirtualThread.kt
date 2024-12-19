package live.lingting.framework.thread

import java.util.concurrent.Callable
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.atomic.AtomicLong
import java.util.function.Supplier
import live.lingting.framework.function.ThrowableRunnable
import live.lingting.framework.thread.executor.ThreadPoolExecutorServiceImpl
import live.lingting.framework.util.ClassUtils

/**
 * @author lingting 2024-09-18 19:59
 */
object VirtualThread {

    @JvmField
    val isSupport: Boolean

    @JvmField
    val instance: VirtualThreadServiceImpl

    init {
        val method = ClassUtils.method(Thread::class.java, "ofVirtual")
        isSupport = method != null
        instance = VirtualThreadServiceImpl()
    }

    @JvmStatic
    fun executor(): ThreadPoolExecutor {
        return instance.executor()
    }

    @JvmStatic
    fun update(executor: ThreadPoolExecutor): VirtualThreadServiceImpl {
        return instance.executor(executor)
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

    @JvmStatic
    fun execute(runnable: ThrowableRunnable) {
        instance.execute(runnable)
    }

    @JvmStatic
    fun execute(name: String, runnable: ThrowableRunnable) {
        instance.execute(name, runnable)
    }

    @JvmStatic
    fun execute(runnable: KeepRunnable) {
        instance.execute(runnable)
    }

    @JvmStatic
    fun <T> async(supplier: Supplier<T>): CompletableFuture<T> {
        return instance.async(supplier)
    }

    @JvmStatic
    fun <T> submit(callable: Callable<T>): Future<T> {
        return instance.submit(callable)
    }
}

class VirtualThreadServiceImpl : ThreadPoolExecutorServiceImpl {

    constructor() : super(
        if (VirtualThread.isSupport) {
            val atomic = AtomicLong()
            ThreadPool.newExecutor { runnable ->
                Thread.ofVirtual().name("tv-${atomic.incrementAndGet()}").unstarted(runnable)
            }
        } else {
            ThreadPool.executor()
        })

    override fun executor(executor: ThreadPoolExecutor): VirtualThreadServiceImpl {
        super.executor(executor)
        return this
    }
}
