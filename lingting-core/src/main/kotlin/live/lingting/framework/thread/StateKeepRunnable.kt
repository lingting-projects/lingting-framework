package live.lingting.framework.thread

import live.lingting.framework.time.DateTime

/**
 * @author lingting 2024-04-29 10:41
 */
abstract class StateKeepRunnable : KeepRunnable {
    protected var thread: Thread? = null

    protected var start: Long = 0

    protected var end: Long = 0

    protected var state: State = State.WAIT

    protected constructor() : super()

    protected constructor(name: String?) : super(name)

    private fun innerOnStart() {
        start = DateTime.millis()
        state = State.RUNNING
        thread = Thread.currentThread()
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
    fun time(): Long {
        if (state == State.WAIT) {
            return 0
        }
        if (state == State.FINISH) {
            return end - start
        }
        return DateTime.millis() - start
    }

    /**
     * 结束
     */
    fun interrupt() {
        if (thread != null && !isFinish && !thread!!.isInterrupted) {
            thread!!.interrupt()
        }
    }

    enum class State {
        WAIT,

        RUNNING,

        FINISH,
    }
}
