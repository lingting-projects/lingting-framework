package live.lingting.framework.value.cycle;

import live.lingting.framework.lock.JavaReentrantLock;


/**
 * @author lingting 2024-02-27 19:19
 */
public abstract class AbstractConcurrentCycleValue<T> extends AbstractCycleValue<T> {

	protected final JavaReentrantLock lock = new JavaReentrantLock();

	@Override

	public void reset() {
		lock.runByInterruptibly(this::doReset);
	}

	@Override

	public T next() {
		return lock.getByInterruptibly(super::next);
	}

	public abstract void doReset();

}
