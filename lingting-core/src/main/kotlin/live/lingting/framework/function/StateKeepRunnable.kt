package live.lingting.framework.function

/**
 * @author lingting 2024-04-29 10:41
 */
abstract class StateKeepRunnable @JvmOverloads constructor(
    name: String? = null,
    mdc: Map<String, String>? = null
) : StateRunnable() {

    protected val keep = KeepRunnableImpl(this, name, mdc)

    var threadName: String
        get() = keep.threadName
        set(value) {
            keep.threadName = value
        }

    protected open fun keepRun() {
        super.run()
    }

    final override fun run() {
        keep.run()
    }

    class KeepRunnableImpl(val r: StateKeepRunnable, name: String? = null, mdc: Map<String, String>? = null) :
        KeepRunnable(name, mdc) {

        override fun doProcess() {
            r.keepRun()
        }

    }

}
