package live.lingting.framework.value.step;

import live.lingting.framework.value.StepValue;
import lombok.RequiredArgsConstructor;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lingting 2024-01-23 15:30
 */
public class IteratorStepValue<T> extends StepValue<T> {

	public IteratorStepValue(Iterator<T> iterator) {
		super(null, new Function<>(iterator));
	}

	@RequiredArgsConstructor
	public static class Function<T> implements StepFunction<T> {

		private final Map<Long, T> map = new ConcurrentHashMap<>();

		private final Iterator<T> iterator;

		@Override
		public T next(long count, T previous) {
			return map.computeIfAbsent(count, k -> iterator.hasNext() ? iterator.next() : null);
		}

	}

}
