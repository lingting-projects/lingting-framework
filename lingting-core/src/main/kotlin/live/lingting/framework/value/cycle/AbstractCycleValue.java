package live.lingting.framework.value.cycle;

import live.lingting.framework.value.CycleValue;

import java.math.BigInteger;

/**
 * @author lingting 2024-02-27 19:19
 */
public abstract class AbstractCycleValue<T> implements CycleValue<T> {

	protected BigInteger count = BigInteger.ZERO;

	@Override
	public BigInteger count() {
		return count;
	}

	@Override
	public T next() {
		count = count.add(BigInteger.ONE);
		return doNext();
	}

	public abstract T doNext();

}
