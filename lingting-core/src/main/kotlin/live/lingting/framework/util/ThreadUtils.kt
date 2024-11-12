package live.lingting.framework.util

import live.lingting.framework.function.ThrowableRunnable
import live.lingting.framework.thread.KeepRunnable
import live.lingting.framework.thread.ThreadService
import live.lingting.framework.thread.VirtualThread
import java.util.concurrent.Callable
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import java.util.function.Supplier

/**
 * @author lingting 2023-11-15 16:44
 */
class ThreadUtils private constructor() {
    init {
        throw UnsupportedOperationException("This is a utility class and cannot be instantiated")
    }

    companion object {
        var instance: ThreadService = VirtualThread.instance()

        fun instance(): ThreadService {
            return instance
        }


        fun executor(): ExecutorService {
            return instance().executor()
        }


        fun execute(runnable: ThrowableRunnable) {
            execute(null, runnable)
        }


        fun execute(name: String?, runnable: ThrowableRunnable) {
            instance().execute(name, runnable)
        }

        fun execute(runnable: KeepRunnable) {
            instance().execute(runnable)
        }

        fun <T> async(supplier: Supplier<T>): CompletableFuture<T> {
            return instance().async(supplier)
        }

        fun <T> submit(callable: Callable<T>): Future<T> {
            return instance().submit(callable)
        }

        fun setInstance(instance: ThreadService) {
            Companion.instance = instance
        }
    }
}
