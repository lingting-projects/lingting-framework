package live.lingting.framework.value.cycle;

import live.lingting.framework.value.StepValue;
import lombok.RequiredArgsConstructor;

/**
 * @author lingting 2024-01-23 15:22
 */
@RequiredArgsConstructor
public class StepCycleValue<T> extends AbstractConcurrentCycleValue<T> {

	private final StepValue<T> step;

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
