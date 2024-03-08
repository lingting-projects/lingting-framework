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

	private long size = 10;

	private T cursor;

	public long getSize() {
		return size < 1 ? 10 : size;
	}

}
