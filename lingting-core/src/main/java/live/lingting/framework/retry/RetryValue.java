package live.lingting.framework.retry;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

/**
 * @author lingting 2023-10-23 18:59
 */
@Getter
@ToString
@EqualsAndHashCode
@SuppressWarnings("java:S112")
public final class RetryValue<T> {

	private final T value;

	private final boolean success;

	private final List<RetryLog<T>> logs;

	/**
	 * @param success 是否执行成功
	 *
	 */
	public RetryValue(T value, boolean success, List<RetryLog<T>> logs) {
		this.value = value;
		this.success = success;
		this.logs = logs;
	}

	public T get() throws Exception {
		if (success()) {
			return value;
		}
		throw logs.get(0).getException();
	}

	public T value() {
		return value;
	}

	public boolean success() {
		return success;
	}

	public List<RetryLog<T>> logs() {
		return logs;
	}

}
