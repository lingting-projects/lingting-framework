package live.lingting.framework.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Collections;
import java.util.List;

/**
 * @author lingting 2024-02-02 17:54
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ScrollResult<T, C> {

	private List<T> records;

	private C cursor;

	private long total = 0;

	public static <T, C> ScrollResult<T, C> of(List<T> collection, C cursor) {
		return new ScrollResult<>(collection, cursor, collection.size());
	}

	public static <T, C> ScrollResult<T, C> empty() {
		return new ScrollResult<>(Collections.emptyList(), null, 0);
	}

}
