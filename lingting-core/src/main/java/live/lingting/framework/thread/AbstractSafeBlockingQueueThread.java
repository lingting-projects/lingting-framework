package live.lingting.framework.thread;

import java.time.Duration;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author lingting 2024-01-03 11:25
 */
public abstract class AbstractSafeBlockingQueueThread<T> extends AbstractSafeQueueThread<T> {

	protected final BlockingQueue<T> queue = new LinkedBlockingQueue<>();

	@Override
	protected void doPut(T t) {
		if (t == null) {
			return;
		}
		try {
			queue.put(t);
		}
		catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			doPut(t);
		}
		catch (Exception e) {
			log.error("{} put Object error, object: {}", getSimpleName(), t, e);
			doPut(t);
		}
	}

	@Override
	protected long queueSize() {
		return queue.size();
	}

	@Override
	protected T poll(Duration timeout) throws InterruptedException {
		return queue.poll(timeout.toMillis(), TimeUnit.MILLISECONDS);
	}

}
