package live.lingting.framework.function

import java.time.Duration

/**
 * @author lingting 2024-01-26 15:34
 */
interface InterruptedRunnable : ThrowingRunnable {

    override fun run()

    companion object {
        fun threadSleep(duration: Duration): InterruptedRunnable {
            return object : InterruptedRunnable {
                override fun run() {
                    Thread.sleep(duration.toMillis())
                }
            }
        }

        val THREAD_SLEEP: InterruptedRunnable = object : InterruptedRunnable {
            override fun run() {
                Thread.sleep(500)
            }
        }
    }
}
