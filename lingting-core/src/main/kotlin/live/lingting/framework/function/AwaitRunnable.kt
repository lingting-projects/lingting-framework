package live.lingting.framework.function

import java.util.function.Predicate
import java.util.function.Supplier

/**
 * @author lingting 2025/1/22 18:05
 */
class AwaitRunnable<S>(
    val supplier: Supplier<S>,
    val predicate: Predicate<S>,
    val sleep: InterruptedRunnable = InterruptedRunnable.THREAD_SLEEP,
) : StateKeepRunnable() {

    var value: S? = null

    var ex: Throwable? = null

    override fun doProcess() {
        try {
            while (thread?.isInterrupted == false && thread?.isAlive == true) {
                val s = supplier.get()
                if (predicate.test(s)) {
                    value = s
                    break
                }
                sleep.run()
            }
        } catch (t: Throwable) {
            ex = t
        }
    }

    fun get(): S? {
        if (ex == null) {
            return value
        }
        throw ex!!
    }


}
