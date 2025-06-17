package live.lingting.framework.concurrent.await

import live.lingting.framework.function.StateKeepRunnable

/**
 * @author lingting 2025/1/22 18:05
 */
class AwaitRunnable<R>(
    val worker: AwaitWorker<R>,
    val predicate: AwaitPredicate<R>,
    val sleep: Runnable,
) : StateKeepRunnable() {

    var value: R? = null

    var ex: Throwable? = null

    fun isRunning() = !isFinish && thread?.isAlive == true && thread?.isInterrupted == false

    override fun doProcess() {
        try {
            while (isRunning()) {
                val duration = duration()
                val r = worker.get(duration)
                if (predicate.test(duration, r)) {
                    value = r
                    break
                }
                sleep.run()
            }
        } catch (t: Throwable) {
            if (t is InterruptedException) {
                interrupt()
            }
            ex = t
        }
    }

    fun get(): R? {
        if (ex == null) {
            return value
        }
        throw ex!!
    }

}
