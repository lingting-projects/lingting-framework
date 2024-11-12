package live.lingting.framework.api;

import java.util.Collections;
import java.util.List;

/**
 * @author lingting 2024-02-02 17:53
 */
public class PaginationResult<T> {

	private long total = 0;

	private List<T> records = Collections.emptyList();

	public PaginationResult(long total, List<T> records) {
		this.total = total;
		this.records = records;
	}

	public PaginationResult() {}

	public long getTotal() {return this.total;}

	public List<T> getRecords() {return this.records;}

	public PaginationResult<T> setTotal(long total) {
		this.total = total;
		return this;
	}

	public PaginationResult<T> setRecords(List<T> records) {
		this.records = records;
		return this;
	}
}
