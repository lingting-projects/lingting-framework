package live.lingting.framework.thread;

import live.lingting.framework.lock.JavaReentrantLock;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author lingting 2023-04-22 10:39
 */
@SuppressWarnings("java:S1066")
public abstract class AbstractDynamicTimer<T> extends AbstractThreadContextComponent {

	protected final JavaReentrantLock lock = new JavaReentrantLock();

	protected final PriorityQueue<T> queue = new PriorityQueue<>(comparator());

	public abstract Comparator<T> comparator();

	/**
	 * 还有多久要处理该对象
	 * @param t 对象
	 * @return 具体处理该对象还要多久, 单位: 毫秒
	 */
	protected abstract long sleepTime(T t);

	public void put(T t) {
		if (t == null) {
			return;
		}

		try {
			lock.runByInterruptibly(() -> {
				queue.add(t);
				lock.signalAll();
			});
		}
		catch (InterruptedException e) {
			interrupt();
		}
		catch (Exception e) {
			log.error("{} put error, param: {}", getSimpleName(), t, e);
		}
	}

	/**
	 * 将取出的元素重新放入队列
	 */
	public void replay(T t) {
		put(t);
	}

	@Override
	protected void doRun() throws Exception {
		T t = pool();
		lock.runByInterruptibly(() -> {
			if (t == null) {
				lock.await(24, TimeUnit.HOURS);
				return;
			}

			long sleepTime = sleepTime(t);
			// 需要休眠
			if (sleepTime > 0) {
				// 如果是被唤醒
				if (lock.await(sleepTime, TimeUnit.MILLISECONDS)) {
					replay(t);
					return;
				}
			}

			process(t);
		});
	}

	protected T pool() {
		return queue.poll();
	}

	protected abstract void process(T t);

	@Override
	protected void shutdown() {
		log.warn("类: {}; 线程: {}; 被中断! 剩余数据: {}", getSimpleName(), getId(), queue.size());
	}

}
