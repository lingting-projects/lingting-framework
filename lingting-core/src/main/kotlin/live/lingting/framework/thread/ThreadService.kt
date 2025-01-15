package live.lingting.framework.thread

import java.util.concurrent.Callable
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future
import java.util.concurrent.ThreadPoolExecutor
import java.util.function.Supplier
import live.lingting.framework.function.ThrowableRunnable

/**
 * @author lingting 2024-09-20 13:21
 */
interface ThreadService {

    fun executor(): ThreadPoolExecutor

    val isRunning: Boolean
        get() {
            val executor = executor()
            return !executor.isShutdown && !executor.isTerminated
        }

    fun execute(runnable: ThrowableRunnable) {
        execute(null, runnable)
    }

    fun execute(name: String?, runnable: ThrowableRunnable) {
        execute(object : KeepRunnable(name) {
            override fun process() {
                runnable.run()
            }
        })
    }

    fun execute(runnable: KeepRunnable) {
        executor().execute(runnable)
    }

    fun <T> async(supplier: Supplier<T>): CompletableFuture<T> {
        val executor = executor()
        return CompletableFuture.supplyAsync(supplier, executor)
    }

    fun <T> submit(callable: Callable<T>): Future<T> {
        val executor = executor()
        return executor.submit(callable)
    }

}
