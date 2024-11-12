package live.lingting.framework.retry;

/**
 * @author lingting 2023-10-23 19:15
 */
public class RetryLog<T> {

	private final T value;

	private final Exception exception;

	public RetryLog(T value, Exception exception) {
		this.value = value;
		this.exception = exception;
	}

	public T getValue() {return this.value;}

	public Exception getException() {return this.exception;}
}
