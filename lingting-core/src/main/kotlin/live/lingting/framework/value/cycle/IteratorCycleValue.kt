package live.lingting.framework.value.cycle;

import live.lingting.framework.value.step.IteratorStepValue;

import java.util.Iterator;

/**
 * @author lingting 2024-01-23 15:24
 */
public class IteratorCycleValue<T> extends AbstractCycleValue<T> {

	private final IteratorStepValue<T> step;

	public IteratorCycleValue(Iterator<T> iterator) {
		this(new IteratorStepValue<>(iterator));
	}

	public IteratorCycleValue(IteratorStepValue<T> step) {
		this.step = step;
	}

	@Override
	public void reset() {
		step.reset();
	}

	@Override
	public T doNext() {
		if (!step.hasNext()) {
			step.reset();
		}
		return step.next();
	}

	/**
	 * 移除上一个next返回的元素
	 */
	public void remove() {
		step.remove();
	}

}
