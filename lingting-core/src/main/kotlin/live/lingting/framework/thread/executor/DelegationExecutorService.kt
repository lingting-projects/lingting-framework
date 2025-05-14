package live.lingting.framework.thread.executor

import java.util.concurrent.Callable
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.function.Supplier

/**
 * @author lingting 2025/5/12 10:14
 */
@Suppress("UNCHECKED_CAST")
open class DelegationExecutorService(open var delegator: ExecutorService) : ExecutorService {

    val isRunning
        get() = !(delegator.isTerminated || delegator.isShutdown)

    fun <T> async(supplier: Supplier<T>): CompletableFuture<T> {
        return CompletableFuture.supplyAsync(supplier, delegator)
    }

    fun threadFactory(): ThreadFactory? {
        if (delegator is DelegationExecutorService) {
            return (delegator as DelegationExecutorService).threadFactory()
        }
        if (delegator is ThreadPoolExecutor) {
            return (delegator as ThreadPoolExecutor).threadFactory
        }
        if (delegator is PerThreadExecutor) {
            return (delegator as PerThreadExecutor).factory
        }
        return null
    }

    fun <T : ExecutorService> find(clz: Class<T>): T? {
        if (clz.isAssignableFrom(javaClass)) {
            return this as T
        }

        if (delegator is DelegationExecutorService) {
            return (delegator as DelegationExecutorService).find(clz)
        }

        if (clz.isAssignableFrom(delegator.javaClass)) {
            return delegator as T
        }
        return null
    }

    override fun shutdown() {
        return delegator.shutdown()
    }

    override fun shutdownNow(): List<Runnable?> {
        return delegator.shutdownNow()
    }

    override fun isShutdown(): Boolean {
        return delegator.isShutdown()
    }

    override fun isTerminated(): Boolean {
        return delegator.isTerminated
    }

    override fun awaitTermination(timeout: Long, unit: TimeUnit): Boolean {
        return delegator.awaitTermination(timeout, unit)
    }

    override fun <T : Any?> submit(task: Callable<T?>): Future<T?> {
        return delegator.submit(task)
    }

    override fun <T : Any?> submit(task: Runnable, result: T?): Future<T?> {
        return delegator.submit(task, result)
    }

    override fun submit(task: Runnable): Future<*> {
        return delegator.submit(task)
    }

    override fun <T : Any?> invokeAll(tasks: Collection<Callable<T?>?>): List<Future<T?>?> {
        return delegator.invokeAll(tasks)
    }

    override fun <T : Any?> invokeAll(
        tasks: Collection<Callable<T?>?>,
        timeout: Long,
        unit: TimeUnit
    ): List<Future<T?>?> {
        return delegator.invokeAll(tasks, timeout, unit)
    }

    override fun <T : Any?> invokeAny(tasks: Collection<Callable<T?>?>): T & Any {
        val t = delegator.invokeAny(tasks)
        return t!!
    }

    override fun <T : Any?> invokeAny(tasks: Collection<Callable<T?>?>, timeout: Long, unit: TimeUnit): T? {
        return delegator.invokeAny(tasks)
    }

    override fun close() {
        delegator.close()
    }

    override fun execute(command: Runnable) {
        delegator.execute(command)
    }

}
