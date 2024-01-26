package live.lingting.framework.value.step;

import live.lingting.framework.lock.JavaReentrantLock;
import live.lingting.framework.value.StepValue;
import lombok.SneakyThrows;

/**
 * @author lingting 2024-01-15 19:21
 */
public class ConcurrentStepValue<T> extends StepValue<T> {

	private final JavaReentrantLock lock = new JavaReentrantLock();

	public ConcurrentStepValue(T startValue, StepFunction<T> function) {
		super(startValue, function);
	}

	@Override
	@SneakyThrows
	@SuppressWarnings("java:S2272")
	public T next() {
		return lock.getByInterruptibly(super::next);
	}

	@Override
	@SneakyThrows
	public void reset() {
		lock.runByInterruptibly(super::reset);
	}

	@Override
	public StepValue<T> copy() {
		return concurrent();
	}

	@Override
	public StepValue<T> start(T startValue) {
		return new ConcurrentStepValue<>(startValue, function);
	}

}
