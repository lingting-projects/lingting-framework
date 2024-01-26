package live.lingting.framework.retry;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author lingting 2023-10-23 19:15
 */
@Getter
@RequiredArgsConstructor
public class RetryLog<T> {

	private final T value;

	private final Exception exception;

}
