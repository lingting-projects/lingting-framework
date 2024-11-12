package live.lingting.framework.api;

import live.lingting.framework.function.ThrowingFunction;
import live.lingting.framework.value.CursorValue;

import java.util.List;

/**
 * @author lingting 2023-12-29 11:32
 */
public class LimitCursor<T> extends CursorValue<T> {

	private final ThrowingFunction<Long, PaginationResult<T>> limit;

	private long index;

	public LimitCursor(ThrowingFunction<Long, PaginationResult<T>> limit) {
		this.limit = limit;
		this.index = 1;
	}


	@Override
	protected List<T> nextBatchData() {
		PaginationResult<T> result = limit.apply(index);
		index++;
		return result.getRecords();
	}

}
