package live.lingting.framework.api;

import lombok.AllArgsConstructor;
import lombok.Data;
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

	private Long page;

	private Long size;

	private List<Sort> sorts;

	/**
	 * 数据起始索引
	 */
	public long start() {
		return (getPage() - 1) * getSize();
	}

	public Long getPage() {
		if (page == null || page < 1) {
			page = 1L;
		}
		return page;
	}

	public Long getSize() {
		if (size == null || size < 1) {
			size = 10L;
		}
		return size;
	}

	public List<Sort> getSorts() {
		if (sorts == null) {
			sorts = new ArrayList<>();
		}
		return sorts;
	}

	@Data
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
