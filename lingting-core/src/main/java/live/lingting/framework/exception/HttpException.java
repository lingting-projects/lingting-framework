package live.lingting.framework.exception;

/**
 * @author lingting 2024-01-29 16:05
 */
public class HttpException extends RuntimeException {

	public HttpException(String message) {
		super(message);
	}

	public HttpException(String message, Throwable cause) {
		super(message, cause);
	}

	public HttpException(Throwable cause) {
		super(cause);
	}

}
