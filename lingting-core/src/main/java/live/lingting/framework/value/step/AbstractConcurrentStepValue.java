package live.lingting.framework.value.step;

import live.lingting.framework.lock.JavaReentrantLock;
import lombok.SneakyThrows;

import java.math.BigInteger;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * @author lingting 2024-01-15 19:21
 */
@SuppressWarnings("java:S2272")
public abstract class AbstractConcurrentStepValue<T> extends AbstractStepValue<T> {

	protected final JavaReentrantLock lock = new JavaReentrantLock();

	@Override
	public T firt() {
		return calculate(BigInteger.ZERO);
	}

	@Override
	@SneakyThrows
	public BigInteger index() {
		return lock.getByInterruptibly(super::index);
	}

	@Override
	@SneakyThrows
	public void reset() {
		lock.runByInterruptibly(super::reset);
	}

	@Override
	@SneakyThrows
	public List<T> values() {
		return lock.getByInterruptibly(super::values);
	}

	@Override
	@SneakyThrows
	public T next() {
		return lock.getByInterruptibly(() -> {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			return doNext();
		});
	}

	@Override
	@SneakyThrows
	public BigInteger increasing() {
		return lock.getByInterruptibly(super::increasing);
	}

	@Override
	@SneakyThrows
	public T calculateNext() {
		return lock.getByInterruptibly(super::calculateNext);
	}

	@Override
	@SneakyThrows
	public T calculate(BigInteger index) {
		return lock.getByInterruptibly(() -> doCalculate(index));
	}

	@Override
	@SneakyThrows
	public boolean hasNext() {
		return lock.getByInterruptibly(this::doHasNext);
	}

	public abstract boolean doHasNext();

	public T doNext() {
		return super.next();
	}

	public abstract T doCalculate(BigInteger index);

}
