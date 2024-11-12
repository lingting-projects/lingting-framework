package live.lingting.framework.thread;

/**
 * 安全队列, 在队列中还存在数据时永远不会停止运行, 即便发起了服务关闭
 *
 * @author lingting 2024-01-03 11:18
 */
public abstract class AbstractSafeQueueThread<E> extends AbstractQueueThread<E> {

	@Override
	public void put(E e) {
		// 已停止运行
		if (!super.isRun()) {
			log.debug("Stopped, but data is being put!");
		}
		doPut(e);
	}

	protected abstract void doPut(E e);

	protected abstract long queueSize();

	@Override
	public boolean isRun() {
		// 在队列中还有数据时, 不停止处理
		return queueSize() > 0 || super.isRun();
	}

	@Override
	public void onApplicationStopBefore() {
		awaitTerminated();
	}

}
