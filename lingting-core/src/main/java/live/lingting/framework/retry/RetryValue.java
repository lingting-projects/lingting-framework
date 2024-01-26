package live.lingting.framework.retry;

import java.util.List;

/**
 * @param success 是否执行成功
 * @author lingting 2023-10-23 18:59
 */
@SuppressWarnings("java:S112")
public record RetryValue<T>(T value, boolean success, List<RetryLog<T>> logs) {

	public T get() throws Exception {
		if (success()) {
			return value;
		}
		throw logs.get(0).getException();
	}

}
