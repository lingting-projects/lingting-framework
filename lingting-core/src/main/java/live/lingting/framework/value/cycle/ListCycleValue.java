package live.lingting.framework.value.cycle;

import live.lingting.framework.util.CollectionUtils;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * @author lingting 2024-04-29 15:45
 */
@RequiredArgsConstructor
public class ListCycleValue<T> extends AbstractConcurrentCycleValue<T> {

	private final List<T> list;

	private int index = -1;

	public boolean isEmpty() {
		return CollectionUtils.isEmpty(list);
	}

	@Override
	public T doNext() {
		index += 1;
		if (index < list.size()) {
			return list.get(index);
		}
		doReset();
		return doNext();
	}


	@Override
	public void doReset() {
		index = -1;
	}
}
