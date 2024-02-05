package live.lingting.framework.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Collections;
import java.util.List;

/**
 * @author lingting 2024-02-02 17:53
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class PaginationResult<T> {

	private Long total = 0L;

	private List<T> records = Collections.emptyList();

}
