package live.lingting.framework.function

import java.time.Duration

/**
 * @author lingting 2024-01-26 15:34
 */
fun interface InterruptedRunnable : ThrowingRunnable {

    companion object {

        @JvmStatic
        fun threadSleep(duration: Duration): InterruptedRunnable {
            return InterruptedRunnable { Thread.sleep(duration.toMillis()) }
        }

        @JvmField
        val THREAD_SLEEP: InterruptedRunnable = InterruptedRunnable { Thread.sleep(50) }

    }

    @Throws(InterruptedException::class)
    override fun run()
}
