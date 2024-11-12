package live.lingting.framework.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

/**
 * @author lingting 2023-04-22 10:55
 */
public class JavaReentrantLock {

	/**
	 * 锁
	 */
	protected final ReentrantLock lock = new ReentrantLock();

	/**
	 * 激活与休眠线程
	 */
	protected final Condition defaultCondition = lock.newCondition();

	public Condition newCondition() {
		return getLock().newCondition();
	}

	public void lock() {
		getLock().lock();
	}

	public void lockInterruptibly() throws InterruptedException {
		getLock().lockInterruptibly();
	}

	public boolean lockTry() throws InterruptedException {
		return lockTry(1, TimeUnit.MILLISECONDS);
	}

	public boolean lockTry(long timeout, TimeUnit unit) throws InterruptedException {
		return getLock().tryLock(timeout, unit);
	}

	public void run(Runnable runnable) {
		lock();
		try {
			runnable.run();
		}
		finally {
			unlock();
		}
	}

	public void runByInterruptibly(LockRunnable runnable) throws InterruptedException {
		lockInterruptibly();
		try {
			runnable.run();
		}
		finally {
			unlock();
		}
	}

	public void runByTry(LockRunnable runnable) throws InterruptedException {
		if (lockTry()) {
			try {
				runnable.run();
			}
			finally {
				unlock();
			}
		}
	}

	public void runByTry(LockRunnable runnable, long timeout, TimeUnit unit) throws InterruptedException {
		if (lockTry(timeout, unit)) {
			try {
				runnable.run();
			}
			finally {
				unlock();
			}
		}
	}

	public <R> R get(Supplier<R> runnable) {
		ReentrantLock reentrantLock = getLock();
		reentrantLock.lock();
		try {
			return runnable.get();
		}
		finally {
			reentrantLock.unlock();
		}
	}

	public <R> R getByInterruptibly(LockSupplier<R> runnable) throws InterruptedException {
		ReentrantLock reentrantLock = getLock();
		reentrantLock.lockInterruptibly();
		try {
			return runnable.get();
		}
		finally {
			reentrantLock.unlock();
		}
	}

	public void unlock() {
		getLock().unlock();
	}

	public void signal() throws InterruptedException {
		runByInterruptibly(() -> getDefaultCondition().signal());
	}

	public void signalAll() throws InterruptedException {
		runByInterruptibly(() -> getDefaultCondition().signalAll());
	}

	public void await() throws InterruptedException {
		runByInterruptibly(() -> getDefaultCondition().await());
	}

	/**
	 * @return 是否被唤醒
	 */
	public boolean await(long time, TimeUnit timeUnit) throws InterruptedException {
		return getByInterruptibly(() -> getDefaultCondition().await(time, timeUnit));
	}

	public ReentrantLock getLock() {return this.lock;}

	public Condition getDefaultCondition() {return this.defaultCondition;}
}
