package live.lingting.framework.api;

import live.lingting.framework.function.ThrowingFunction;
import live.lingting.framework.util.CollectionUtils;
import live.lingting.framework.value.CursorValue;

import java.util.List;

/**
 * @author lingting 2023-12-29 11:32
 */
public class ScrollCursor<T, S> extends CursorValue<T> {

	private final ThrowingFunction<S, ScrollResult<T, S>> scroll;

	private S scrollId;

	public ScrollCursor(ThrowingFunction<S, ScrollResult<T, S>> scroll, S scrollId, List<T> data) {
		this.scroll = scroll;
		this.scrollId = scrollId;
		// 初始数据就为空, 直接结束
		if (CollectionUtils.isEmpty(data)) {
			empty = true;
		}
		else {
			current.addAll(data);
		}
	}


	@Override
	protected List<T> nextBatchData() {
		ScrollResult<T, S> result = scroll.apply(scrollId);
		scrollId = result.getCursor();
		return result.getRecords();
	}

}
