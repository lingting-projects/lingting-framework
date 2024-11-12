package live.lingting.framework.thread

import live.lingting.framework.function.ThrowableRunnable
import live.lingting.framework.util.ClassUtils
import java.lang.reflect.Method
import java.util.concurrent.Callable
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.function.Supplier

/**
 * @author lingting 2024-09-18 19:59
 */
class VirtualThread private constructor() {
    init {
        throw UnsupportedOperationException("This is a utility class and cannot be instantiated")
    }

    class Impl : ThreadService {
        protected var executor: ExecutorService

        init {
            // 如果不支持虚拟线程则使用线程池
            this.executor = if (isSupport) Executors.newVirtualThreadPerTaskExecutor() else ThreadPool.executor()
        }

        override fun executor(): ExecutorService {
            return executor
        }

        fun executor(executor: ExecutorService): Impl {
            this.executor = executor
            return this
        }
    }

    companion object {
        val isSupport: Boolean

        val instance: Impl

        init {
            val method: Method = ClassUtils.method(Thread::class.java, "ofVirtual")
            isSupport = method != null
            instance = Impl()
        }

        fun instance(): Impl {
            return instance
        }


        fun executor(): ExecutorService {
            return instance.executor()
        }

        fun update(executor: ExecutorService): Impl {
            return instance().executor(executor)
        }

        val isRunning: Boolean
            /**
             * 线程池是否运行中
             */
            get() = instance.isRunning

        fun execute(runnable: ThrowableRunnable?) {
            instance.execute(runnable!!)
        }

        fun execute(name: String?, runnable: ThrowableRunnable?) {
            instance.execute(name, runnable!!)
        }

        fun execute(runnable: KeepRunnable?) {
            instance.execute(runnable!!)
        }

        fun <T> async(supplier: Supplier<T>): CompletableFuture<T?> {
            return instance.async(supplier)
        }

        fun <T> submit(callable: Callable<T>): Future<T?> {
            return instance.submit(callable)
        }
    }
}
