package live.lingting.framework.context

/**
 * @author lingting 2023-12-06 17:13
 */
object ContextHolder {
    var isStop: Boolean = true
        private set

    fun start() {
        isStop = false
    }

    fun stop() {
        isStop = true
    }

}
