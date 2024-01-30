package live.lingting.framework.datascope.exception;

/**
 * @author lingting 2024-01-19 15:54
 */
public class DataScopeException extends RuntimeException {

	public DataScopeException(String message) {
		super(message);
	}

	public DataScopeException(String message, Throwable cause) {
		super(message, cause);
	}

}
