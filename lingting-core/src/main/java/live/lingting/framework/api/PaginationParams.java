package live.lingting.framework.api;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lingting 2024-02-02 17:53
 */

public class PaginationParams {

	private long page = 1;

	private long size = 10;

	private List<Sort> sorts;

	public PaginationParams(long page, long size, List<Sort> sorts) {
		this.page = page;
		this.size = size;
		this.sorts = sorts;
	}

	public PaginationParams() {}

	/**
	 * 数据起始索引
	 */
	public long start() {
		return (getPage() - 1) * getSize();
	}

	public long getPage() {
		if (page < 1) {
			page = 1;
		}
		return page;
	}

	public long getSize() {
		if (size < 1) {
			size = 10;
		}
		return size;
	}

	public List<Sort> getSorts() {
		if (sorts == null) {
			sorts = new ArrayList<>();
		}
		return sorts;
	}

	public PaginationParams setPage(long page) {
		this.page = page;
		return this;
	}

	public PaginationParams setSize(long size) {
		this.size = size;
		return this;
	}

	public PaginationParams setSorts(List<Sort> sorts) {
		this.sorts = sorts;
		return this;
	}


	public static class Sort {

		/**
		 * 排序字段
		 */
		private String field;

		/**
		 * 是否倒序
		 */
		private Boolean desc;

		public Sort(String field, Boolean desc) {
			this.field = field;
			this.desc = desc;
		}

		public Sort() {}

		public String getField() {return this.field;}

		public Boolean getDesc() {return this.desc;}

		public Sort setField(String field) {
			this.field = field;
			return this;
		}

		public Sort setDesc(Boolean desc) {
			this.desc = desc;
			return this;
		}
	}

}
