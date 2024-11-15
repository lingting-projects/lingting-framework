package live.lingting.framework.thread

import java.util.concurrent.Callable
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.function.Supplier
import live.lingting.framework.function.ThrowableRunnable
import live.lingting.framework.util.ClassUtils

/**
 * @author lingting 2024-09-18 19:59
 */
object VirtualThread {

    @JvmStatic
    val isSupport: Boolean

    @JvmStatic
    val instance: VirtualThreadServiceImpl

    init {
        val method = ClassUtils.method(Thread::class.java, "ofVirtual")
        isSupport = method != null
        instance = VirtualThreadServiceImpl()
    }

    @JvmStatic
    fun instance(): VirtualThreadServiceImpl {
        return instance
    }

    @JvmStatic
    fun executor(): ExecutorService {
        return instance.executor()
    }

    @JvmStatic
    fun update(executor: ExecutorService): VirtualThreadServiceImpl {
        return instance().executor(executor)
    }

    @JvmStatic
    val isRunning: Boolean
        /**
         * 线程池是否运行中
         */
        get() = instance.isRunning

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

class VirtualThreadServiceImpl : ThreadService {
    protected var executor: ExecutorService

    init {
        // 如果不支持虚拟线程则使用线程池
        this.executor = if (VirtualThread.isSupport) Executors.newVirtualThreadPerTaskExecutor() else ThreadPool.executor()
    }

    override fun executor(): ExecutorService {
        return executor
    }

    fun executor(executor: ExecutorService): VirtualThreadServiceImpl {
        this.executor = executor
        return this
    }
}
