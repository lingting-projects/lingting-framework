package live.lingting.framework.function

import live.lingting.framework.time.DateTime
import live.lingting.framework.util.DurationUtils.millis
import java.time.Duration

/**
 * @author lingting 2024-04-29 10:41
 */
abstract class StateKeepRunnable @JvmOverloads constructor(
    name: String? = null,
    mdc: Map<String, String>? = null
) : KeepRunnable(name, mdc) {

    protected var start: Long = 0

    protected var end: Long = 0

    protected var state: State = State.WAIT

    private fun innerOnStart() {
        start = DateTime.millis()
        state = State.RUNNING
    }

    protected open fun onStart() {
        //
    }

    override fun process() {
        innerOnStart()
        onStart()
        doProcess()
    }

    protected abstract fun doProcess()

    override fun onFinally() {
        end = DateTime.millis()
        state = State.FINISH
        onEnd()
    }

    protected open fun onEnd() {
        //
    }

    val isFinish: Boolean
        get() = state == State.FINISH

    /**
     * 执行时长, 单位: 毫秒
     */
    fun duration(): Duration {
        if (state == State.WAIT) {
            return Duration.ZERO
        }
        val e = if (state == State.FINISH) end else DateTime.millis()
        return (e - start).millis
    }

    enum class State {
        WAIT,

        RUNNING,

        FINISH,
    }

}
