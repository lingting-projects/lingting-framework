package live.lingting.framework.util

import java.util.concurrent.Callable
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future
import java.util.concurrent.ThreadPoolExecutor
import java.util.function.Supplier
import live.lingting.framework.function.ThrowableRunnable
import live.lingting.framework.thread.KeepRunnable
import live.lingting.framework.thread.ThreadService
import live.lingting.framework.thread.VirtualThread

/**
 * @author lingting 2023-11-15 16:44
 */
object ThreadUtils {
    @JvmStatic
    var instance: ThreadService = VirtualThread.instance

    @JvmStatic
    fun instance(): ThreadService {
        return instance
    }

    @JvmStatic
    fun executor(): ThreadPoolExecutor {
        return instance().executor()
    }

    @JvmStatic
    fun execute(runnable: ThrowableRunnable) {
        execute(null, runnable)
    }

    @JvmStatic
    fun execute(name: String?, runnable: ThrowableRunnable) {
        instance().execute(name, runnable)
    }

    @JvmStatic
    fun execute(runnable: KeepRunnable) {
        instance().execute(runnable)
    }

    @JvmStatic
    fun <T> async(supplier: Supplier<T>): CompletableFuture<T> {
        return instance().async(supplier)
    }

    @JvmStatic
    fun <T> submit(callable: Callable<T>): Future<T> {
        return instance().submit(callable)
    }

}

