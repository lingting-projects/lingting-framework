package live.lingting.framework.thread

/**
 * @author lingting 2022/6/27 20:26
 */
abstract class AbstractSafeTimer : AbstractTimer() {
    override fun onApplicationStopBefore() {
        awaitTerminated()
    }
}
