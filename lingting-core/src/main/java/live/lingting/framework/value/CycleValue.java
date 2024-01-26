package live.lingting.framework.value;

import live.lingting.framework.value.cycle.CycleValueFunction;
import live.lingting.framework.value.cycle.IteratorCycleValue;
import live.lingting.framework.value.cycle.StepCycleValueFunction;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigInteger;
import java.util.Iterator;

/**
 * 循环数据
 *
 * @author lingting 2023-12-19 16:01
 */
@Getter
@RequiredArgsConstructor
public class CycleValue<T> {

	protected final CycleValueFunction<T> function;

	/**
	 * 已执行次数
	 */
	protected BigInteger count = BigInteger.ZERO;

	public static <T> CycleValue<T> step(StepValue<T> step) {
		return new CycleValue<>(new StepCycleValueFunction<>(step.copy()));
	}

	public static <T> CycleValue<T> iterator(Iterator<T> iterator) {
		return new IteratorCycleValue<>(iterator);
	}

	public T next() {
		if (!function.hasNext()) {
			function.reset();
		}
		return function.next();
	}

}
