package live.lingting.framework.value.cycle;

import live.lingting.framework.value.CycleValue;
import live.lingting.framework.value.StepValue;

import java.util.Iterator;

/**
 * @author lingting 2024-01-23 15:24
 */
public class IteratorCycleValue<T> extends CycleValue<T> {

	public IteratorCycleValue(Iterator<T> iterator) {
		super(new StepCycleValueFunction<>(StepValue.iterator(iterator)));
	}

}
