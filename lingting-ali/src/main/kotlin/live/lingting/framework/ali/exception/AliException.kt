package live.lingting.framework.ali.exception;

/**
 * @author lingting 2024-09-12 21:40
 */
public class AliException extends RuntimeException {

	public AliException(String message) {
		super(message);
	}

	public AliException(String message, Throwable cause) {
		super(message, cause);
	}

}
