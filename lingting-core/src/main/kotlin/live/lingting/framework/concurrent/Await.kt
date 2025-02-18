package live.lingting.framework.concurrent

import java.time.Duration
import java.util.concurrent.ExecutorService
import java.util.concurrent.TimeoutException
import java.util.concurrent.atomic.AtomicLong
import java.util.function.Predicate
import java.util.function.Supplier
import live.lingting.framework.function.AwaitRunnable
import live.lingting.framework.function.InterruptedRunnable
import live.lingting.framework.util.ThreadUtils
import live.lingting.framework.util.ValueUtils

/**
 * @author lingting 2025/1/22 17:33
 */
open class Await<S> @JvmOverloads constructor(
    val timeout: Duration?,
    val supplier: Supplier<S?>,
    val predicate: Predicate<S?>,
    val sleep: InterruptedRunnable = SLEEP,
    val executor: ExecutorService = ThreadUtils.executor()
) {

    companion object {

        val SLEEP: InterruptedRunnable = InterruptedRunnable.THREAD_SLEEP

        private val atomic = AtomicLong()

        @JvmStatic
        fun wait(duration: Duration) {
            Thread.sleep(duration.toMillis())
        }

        @JvmStatic
        @JvmOverloads
        @Throws(TimeoutException::class)
        fun waitTrue(timeout: Duration? = null, sleep: InterruptedRunnable = SLEEP, supplier: Supplier<Boolean?>): Boolean {
            wait(timeout, sleep, supplier) { it == true }
            return true
        }

        @JvmStatic
        @JvmOverloads
        @Throws(TimeoutException::class)
        fun waitFalse(timeout: Duration? = null, sleep: InterruptedRunnable = SLEEP, supplier: Supplier<Boolean?>): Boolean {
            wait(timeout, sleep, supplier) { it == false }
            return false
        }

        @JvmStatic
        @JvmOverloads
        @Throws(TimeoutException::class)
        fun <S> waitNull(timeout: Duration? = null, sleep: InterruptedRunnable = SLEEP, supplier: Supplier<S?>): S? {
            wait(timeout, sleep, supplier) { it == null }
            return null
        }

        @JvmStatic
        @JvmOverloads
        @Throws(TimeoutException::class)
        fun <S> waitNotNull(timeout: Duration? = null, sleep: InterruptedRunnable = SLEEP, supplier: Supplier<S?>): S {
            val s = wait(timeout, sleep, supplier) { it != null }
            return s!!
        }

        @JvmStatic
        @JvmOverloads
        @Throws(TimeoutException::class)
        fun <S> waitPresent(timeout: Duration? = null, sleep: InterruptedRunnable = SLEEP, supplier: Supplier<S?>): S {
            val s = wait(timeout, sleep, supplier) { ValueUtils.isPresent(it) }
            return s!!
        }

        @JvmStatic
        @JvmOverloads
        @Throws(TimeoutException::class)
        fun <S> wait(
            timeout: Duration? = null, sleep: InterruptedRunnable = SLEEP,
            supplier: Supplier<S?>, predicate: Predicate<S?>
        ): S? {
            return Await(timeout, supplier, predicate, sleep).await()
        }

    }

    var interruptOnTimeout = true

    @Throws(TimeoutException::class)
    fun await(): S? {
        val name = "await-${atomic.andIncrement}"
        val r = AwaitRunnable(supplier, predicate)

        if (timeout == null || !timeout.isPositive) {
            r.run()
            return r.get()
        }

        r.name = name
        executor.submit(r)
        var duration = Duration.ZERO
        try {
            while (duration < timeout) {
                if (r.isFinish) {
                    return r.get()
                }
                sleep.run()
                duration = r.duration()
            }
        } catch (e: InterruptedException) {
            r.interrupt()
            throw e
        }
        if (interruptOnTimeout) {
            r.interrupt()
        }
        throw TimeoutException("current duration: $duration, timeout: $timeout, isFinish: ${r.isFinish}")
    }


}
