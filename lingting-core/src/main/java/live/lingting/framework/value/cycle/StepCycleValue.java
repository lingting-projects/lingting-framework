package live.lingting.framework.value.cycle;

import live.lingting.framework.value.StepValue;

/**
 * @author lingting 2024-01-23 15:22
 */
public class StepCycleValue<T> extends AbstractConcurrentCycleValue<T> {

	private final StepValue<T> step;

	public StepCycleValue(StepValue<T> step) {
		this.step = step;
	}

	@Override
	public void doReset() {
		step.reset();
	}

	@Override
	public T doNext() {
		if (!step.hasNext()) {
			step.reset();
		}
		return step.next();
	}

}
