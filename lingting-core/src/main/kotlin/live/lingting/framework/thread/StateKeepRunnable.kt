package live.lingting.framework.thread

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

    override fun process() {
        start = System.currentTimeMillis()
        state = State.RUNNING
        thread = Thread.currentThread()
        doProcess()
    }

    protected abstract fun doProcess()

    override fun onFinally() {
        end = System.currentTimeMillis()
        state = State.FINISH
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
        return System.currentTimeMillis() - start
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
