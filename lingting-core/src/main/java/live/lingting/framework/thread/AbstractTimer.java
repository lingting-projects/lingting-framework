package live.lingting.framework.thread;

import live.lingting.framework.lock.JavaReentrantLock;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * @author lingting 2022/6/27 20:26
 */
public abstract class AbstractTimer extends AbstractThreadContextComponent {

	protected final JavaReentrantLock lock = new JavaReentrantLock();

	/**
	 * 获取超时时间, 单位: 毫秒
	 */
	public Duration getTimeout() {
		return Duration.ofSeconds(30);
	}

	/**
	 * 执行任务
	 */
	@SuppressWarnings("java:S112")
	protected abstract void process() throws Exception;

	protected void doRun() throws Exception {
		lock.lockInterruptibly();
		try {
			process();
		}
		finally {
			Duration duration = getTimeout();
			lock.await(duration.toMillis(), TimeUnit.MILLISECONDS);
			lock.unlock();
		}
	}

	/**
	 * 唤醒定时器, 立即执行代码
	 */
	public void wake() throws InterruptedException {
		lock.runByTry(lock::signalAll);
	}

}
