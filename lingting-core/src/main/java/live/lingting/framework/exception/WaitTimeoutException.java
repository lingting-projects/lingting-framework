package live.lingting.framework.exception;

/**
 * @author lingting 2023-03-16 19:50
 */
public class WaitTimeoutException extends RuntimeException {

	public WaitTimeoutException() {
		super();
	}

	public WaitTimeoutException(String message) {
		super(message);
	}

	public WaitTimeoutException(String message, Throwable cause) {
		super(message, cause);
	}

	public WaitTimeoutException(Throwable cause) {
		super(cause);
	}

	protected WaitTimeoutException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
