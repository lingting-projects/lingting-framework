package live.lingting.framework.function

import live.lingting.framework.time.DateTime
import live.lingting.framework.util.DurationUtils.millis
import java.time.Duration

/**
 * @author lingting 2024-04-29 10:41
 */
abstract class StateRunnable : Runnable {

    protected var start: Long = 0

    protected var end: Long = 0

    protected var state: State = State.WAIT

    protected var thread: Thread? = null

    val isFinish: Boolean
        get() = state == State.FINISH

    override fun run() {
        check(state == State.WAIT) { "runnable running." }
        this.thread = Thread.currentThread()
        start = DateTime.millis()
        state = State.RUNNING
        try {
            onStart()
            doProcess()
        } finally {
            end = DateTime.millis()
            state = State.FINISH
            this.thread = null
            onFinally()
        }
    }

    protected open fun onStart() {
        //
    }

    protected abstract fun doProcess()

    protected open fun onFinally() {
        //
    }

    /**
     * 中断
     */
    open fun interrupt() {
        thread?.also {
            if (!it.isInterrupted && it.isAlive) {
                it.interrupt()
            }
        }
    }

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
