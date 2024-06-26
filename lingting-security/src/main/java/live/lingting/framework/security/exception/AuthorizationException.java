package live.lingting.framework.security.exception;

/**
 * @author lingting 2023-03-29 21:41
 */
public class AuthorizationException extends SecurityException {

	public AuthorizationException() {
		super();
	}

	public AuthorizationException(String message) {
		super(message);
	}

	public AuthorizationException(String message, Throwable cause) {
		super(message, cause);
	}

}
