package live.lingting.framework.api;

import java.util.Collections;
import java.util.List;

/**
 * @author lingting 2024-02-02 17:54
 */

public class ScrollResult<T, C> {

	private List<T> records;

	private C cursor;

	private long total = 0;

	public ScrollResult(List<T> records, C cursor, long total) {
		this.records = records;
		this.cursor = cursor;
		this.total = total;
	}

	public ScrollResult() {}

	public static <T, C> ScrollResult<T, C> of(List<T> collection, C cursor) {
		return new ScrollResult<>(collection, cursor, collection.size());
	}

	public static <T, C> ScrollResult<T, C> empty() {
		return new ScrollResult<>(Collections.emptyList(), null, 0);
	}

	public List<T> getRecords() {return this.records;}

	public C getCursor() {return this.cursor;}

	public long getTotal() {return this.total;}

	public ScrollResult<T, C> setRecords(List<T> records) {
		this.records = records;
		return this;
	}

	public ScrollResult<T, C> setCursor(C cursor) {
		this.cursor = cursor;
		return this;
	}

	public ScrollResult<T, C> setTotal(long total) {
		this.total = total;
		return this;
	}
}
