package live.lingting.framework.value.step;

import live.lingting.framework.value.StepValue;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * @author lingting 2024-02-27 14:11
 */
public abstract class AbstractStepValue<T> implements StepValue<T> {

	public static final BigInteger DEFAULT_INDEX = BigInteger.valueOf(0);

	protected BigInteger index = DEFAULT_INDEX;

	protected List<T> values;

	@Override
	public BigInteger index() {
		return index;
	}

	@Override
	public void reset() {
		index = DEFAULT_INDEX;
	}

	@Override
	public List<T> values() {
		if (values != null) {
			return values;
		}
		List<T> list = new ArrayList<>();
		StepValue<T> copy = copy();
		while (copy.hasNext()) {
			list.add(copy.next());
		}
		values = list;
		return list;
	}

	@Override
	public T next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		BigInteger increasing = increasing();
		if (increasing.compareTo(BigInteger.ZERO) == 0) {
			return firt();
		}
		return calculate(increasing);
	}

	public BigInteger increasing() {
		BigInteger current = index.add(BigInteger.ONE);
		index = current;
		return current;
	}

	public T calculateNext() {
		return calculate(index.add(BigInteger.ONE));
	}

	public abstract T calculate(BigInteger index);

}
