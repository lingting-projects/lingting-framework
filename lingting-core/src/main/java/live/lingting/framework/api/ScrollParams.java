package live.lingting.framework.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author lingting 2024-02-02 17:54
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ScrollParams<T> {

	private Long size = 10L;

	private T cursor;

	public Long getSize() {
		return size == null || size < 1 ? 10 : size;
	}

}
