package live.lingting.framework.aws.exception;

/**
 * @author lingting 2024-09-19 15:14
 */
public class AwsException extends RuntimeException {

	public AwsException(String message) {
		super(message);
	}

	public AwsException(String message, Throwable cause) {
		super(message, cause);
	}

}
