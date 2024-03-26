package live.lingting.framework.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lingting 2024-02-02 17:53
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class PaginationParams {

	private long page = 1;

	private long size = 10;

	private List<Sort> sorts;

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

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@Accessors(chain = true)
	public static class Sort {

		/**
		 * 排序字段
		 */
		private String field;

		/**
		 * 是否倒序
		 */
		private Boolean desc;

	}

}
