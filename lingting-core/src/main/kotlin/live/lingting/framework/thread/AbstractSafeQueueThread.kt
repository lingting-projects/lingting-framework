package live.lingting.framework.thread

/**
 * 安全队列, 在队列中还存在数据时永远不会停止运行, 即便发起了服务关闭
 * @author lingting 2024-01-03 11:18
 */
abstract class AbstractSafeQueueThread<E> : AbstractQueueThread<E>() {

    override val isRun: Boolean
        // 在队列中还有数据时, 不停止处理
        get() = queueSize() > 0 || super.isRun

    override fun put(e: E?) {
        // 已停止运行
        if (!super.isRun) {
            log.debug("Stopped, but data is being put!")
        }
        doPut(e)
    }

    protected abstract fun doPut(e: E?)

    protected abstract fun queueSize(): Long

    override fun onApplicationStopBefore() {
        awaitTerminated()
    }

}
