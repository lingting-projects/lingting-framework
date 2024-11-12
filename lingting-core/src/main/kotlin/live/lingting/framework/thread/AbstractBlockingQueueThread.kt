package live.lingting.framework.thread;

import java.time.Duration;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 抽象的线程类，主要用于汇聚详情数据 做一些基础的处理后 进行批量插入
 *
 * @author lingting
 */
public abstract class AbstractBlockingQueueThread<T> extends AbstractQueueThread<T> {

	protected final BlockingQueue<T> queue = new LinkedBlockingQueue<>();

	@Override
	public void put(T t) {
		if (t != null) {
			try {
				queue.put(t);
			}
			catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			catch (Exception e) {
				log.error("{} put Object error, object: {}", getSimpleName(), t, e);
			}
		}
	}

	@Override
	protected T poll(Duration timeout) throws InterruptedException {
		return queue.poll(timeout.toMillis(), TimeUnit.MILLISECONDS);
	}

}
