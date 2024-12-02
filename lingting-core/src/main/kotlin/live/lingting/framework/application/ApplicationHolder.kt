package live.lingting.framework.application

/**
 * @author lingting 2023-12-06 17:13
 */
object ApplicationHolder {

    var isStop: Boolean = true
        private set

    fun start() {
        isStop = false
    }

    fun stop() {
        isStop = true
    }

}
