package live.lingting.framework.thread

import live.lingting.framework.function.InterruptedRunnable
import live.lingting.framework.util.ThreadUtils
import java.time.Duration
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorService
import java.util.concurrent.TimeUnit
import java.util.function.Predicate
import java.util.function.Supplier

/**
 * @author lingting 2024-05-31 11:14
 */
class Await<S>(private val supplier: Supplier<S>?, private val predicate: Predicate<S>?, private val sleep: InterruptedRunnable, private val timeout: Duration?, private val executor: ExecutorService?) {
    fun await(): S {
        val supply = Supplier<S> {
            while (true) {
                val s = supplier!!.get()
                if (predicate!!.test(s)) {
                    return@Supplier s
                }

                sleep.run()
            }
        }
        // 未设置超时
        if (timeout == null || timeout.isNegative || timeout.isZero) {
            return supply.get()
        }

        try {
            // 设置超时
            val future = CompletableFuture.supplyAsync(supply, executor)
                .orTimeout(timeout.toMillis(), TimeUnit.MILLISECONDS)
            return future.get()
        } catch (e: ExecutionException) {
            throw e.cause!!
        }
    }

    class AwaitBuilder<S> {
        private var supplier: Supplier<S>? = null

        private var predicate: Predicate<S>? = null

        private var sleep: InterruptedRunnable = InterruptedRunnable.THREAD_SLEEP

        private var timeout: Duration? = null

        private var executor: ExecutorService? = VirtualThread.executor()

        fun supplier(supplier: Supplier<S>?): AwaitBuilder<S> {
            this.supplier = supplier
            return this
        }

        fun predicate(predicate: Predicate<S>?): AwaitBuilder<S> {
            this.predicate = predicate
            return this
        }

        fun sleep(sleep: InterruptedRunnable): AwaitBuilder<S> {
            this.sleep = sleep
            return this
        }

        fun timeout(timeout: Duration?): AwaitBuilder<S> {
            this.timeout = timeout
            return this
        }

        fun executor(executor: ExecutorService?): AwaitBuilder<S> {
            this.executor = executor
            return this
        }

        fun useThreadPool(): AwaitBuilder<S> {
            return executor(ThreadUtils.executor())
        }

        fun useThreadVirtual(): AwaitBuilder<S> {
            return executor(VirtualThread.executor())
        }

        fun build(): Await<S> {
            return Await(supplier, predicate, sleep, timeout, executor)
        }

        fun await(): S {
            return build().await()
        }
    }

    companion object {
        fun <S> builder(supplier: Supplier<S>?, predicate: Predicate<S>?): AwaitBuilder<S> {
            return builder<S>().supplier(supplier).predicate(predicate)
        }

        fun <S> builder(): AwaitBuilder<S> {
            return AwaitBuilder()
        }
    }
}
