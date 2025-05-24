package live.lingting.framework.concurrent

import live.lingting.framework.concurrent.await.AwaitBuilder
import live.lingting.framework.concurrent.await.AwaitBuilder.Companion.defaultExecutor
import live.lingting.framework.concurrent.await.AwaitBuilder.Companion.defaultSleep
import live.lingting.framework.concurrent.await.AwaitOnTimeout
import live.lingting.framework.concurrent.await.AwaitPredicate
import live.lingting.framework.concurrent.await.AwaitRunnable
import live.lingting.framework.concurrent.await.AwaitWorker
import live.lingting.framework.util.ValueUtils
import java.time.Duration
import java.util.concurrent.Executor
import java.util.concurrent.TimeoutException

/**
 * @author lingting 2025/1/22 17:33
 */
open class Await<R>(
    val timeout: Duration?,
    val worker: AwaitWorker<R>,
    val predicate: AwaitPredicate<R>,
    val name: String,
    val sleep: Runnable,
    val onTimeout: AwaitOnTimeout<R>,
    val executor: Executor,
) {

    companion object {

        fun <R> builder() = AwaitBuilder<R>()

        @JvmStatic
        fun wait(duration: Duration) {
            Thread.sleep(duration.toMillis())
        }

        @JvmStatic
        @JvmOverloads
        @Throws(TimeoutException::class)
        fun waitTrue(
            timeout: Duration? = null,
            sleep: Runnable = defaultSleep,
            executor: Executor = defaultExecutor,
            worker: AwaitWorker<Boolean>
        ) {
            waitTrueBuilder(timeout, sleep, executor, worker).build().await()
        }

        @JvmStatic
        @JvmOverloads
        @Throws(TimeoutException::class)
        fun waitTrueBuilder(
            timeout: Duration? = null,
            sleep: Runnable = defaultSleep,
            executor: Executor = defaultExecutor,
            worker: AwaitWorker<Boolean>
        ): AwaitBuilder<Boolean> {
            return waitBuilder(timeout, sleep, executor, worker) { _, r -> r == true }
        }

        @JvmStatic
        @JvmOverloads
        @Throws(TimeoutException::class)
        fun waitFalse(
            timeout: Duration? = null,
            sleep: Runnable = defaultSleep,
            executor: Executor = defaultExecutor,
            worker: AwaitWorker<Boolean>
        ) {
            waitFalseBuilder(timeout, sleep, executor, worker).build().await()
        }

        @JvmStatic
        @JvmOverloads
        @Throws(TimeoutException::class)
        fun waitFalseBuilder(
            timeout: Duration? = null,
            sleep: Runnable = defaultSleep,
            executor: Executor = defaultExecutor,
            worker: AwaitWorker<Boolean>
        ): AwaitBuilder<Boolean> {
            return waitBuilder(timeout, sleep, executor, worker) { _, r -> r == false }
        }

        @JvmStatic
        @JvmOverloads
        @Throws(TimeoutException::class)
        fun <R> waitNull(
            timeout: Duration? = null,
            sleep: Runnable = defaultSleep,
            executor: Executor = defaultExecutor,
            worker: AwaitWorker<R>
        ) {
            waitNullBuilder(timeout, sleep, executor, worker).build().await()
        }

        @JvmStatic
        @JvmOverloads
        @Throws(TimeoutException::class)
        fun <R> waitNullBuilder(
            timeout: Duration? = null,
            sleep: Runnable = defaultSleep,
            executor: Executor = defaultExecutor,
            worker: AwaitWorker<R>
        ): AwaitBuilder<R> {
            return waitBuilder(timeout, sleep, executor, worker) { _, r -> r == null }
        }

        @JvmStatic
        @JvmOverloads
        @Throws(TimeoutException::class)
        fun <R> waitNotNull(
            timeout: Duration? = null,
            sleep: Runnable = defaultSleep,
            executor: Executor = defaultExecutor,
            worker: AwaitWorker<R>
        ): R {
            return waitNotNullBuilder(timeout, sleep, executor, worker).build().await()!!
        }

        @JvmStatic
        @JvmOverloads
        @Throws(TimeoutException::class)
        fun <R> waitNotNullBuilder(
            timeout: Duration? = null,
            sleep: Runnable = defaultSleep,
            executor: Executor = defaultExecutor,
            worker: AwaitWorker<R>
        ): AwaitBuilder<R> {
            return waitBuilder(timeout, sleep, executor, worker) { _, r -> r != null }
        }

        @JvmStatic
        @JvmOverloads
        @Throws(TimeoutException::class)
        fun <R> waitPresent(
            timeout: Duration? = null,
            sleep: Runnable = defaultSleep,
            executor: Executor = defaultExecutor,
            worker: AwaitWorker<R>
        ): R {
            return waitPresentBuilder(timeout, sleep, executor, worker).build().await()!!
        }

        @JvmStatic
        @JvmOverloads
        @Throws(TimeoutException::class)
        fun <R> waitPresentBuilder(
            timeout: Duration? = null,
            sleep: Runnable = defaultSleep,
            executor: Executor = defaultExecutor,
            worker: AwaitWorker<R>
        ): AwaitBuilder<R> {
            return waitBuilder(timeout, sleep, executor, worker) { _, r -> ValueUtils.isPresent(r) }
        }

        @JvmStatic
        @JvmOverloads
        @Throws(TimeoutException::class)
        fun <R> wait(
            timeout: Duration? = null,
            sleep: Runnable = defaultSleep,
            executor: Executor = defaultExecutor,
            worker: AwaitWorker<R>,
            predicate: AwaitPredicate<R>
        ): R? {
            return waitBuilder(timeout, sleep, executor, worker, predicate).build().await()
        }

        @JvmStatic
        @JvmOverloads
        @Throws(TimeoutException::class)
        fun <R> waitBuilder(
            timeout: Duration? = null,
            sleep: Runnable = defaultSleep,
            executor: Executor = defaultExecutor,
            worker: AwaitWorker<R>,
            predicate: AwaitPredicate<R>
        ): AwaitBuilder<R> {
            return builder<R>().timeout(timeout).sleep(sleep).executor(executor).worker(worker, predicate)
        }

    }

    @Throws(TimeoutException::class)
    fun await(): R? {
        val r = AwaitRunnable(worker, predicate, sleep)
        r.threadName = name

        if (timeout == null || !timeout.isPositive) {
            r.run()
            return r.get()
        }

        executor.execute(r)
        try {
            while (r.duration() < timeout) {
                if (r.isFinish) {
                    return r.get()
                }
                sleep.run()
            }
        } catch (e: InterruptedException) {
            r.interrupt()
            throw e
        }
        onTimeout.on(timeout, r)
        throw TimeoutException("await timeout. expect: $timeout; duration: ${r.duration()}; isFinish: ${r.isFinish} ")
    }


}
