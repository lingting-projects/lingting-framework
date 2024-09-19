package live.lingting.framework.aws.exception;

/**
 * @author lingting 2024-09-19 15:14
 */
public class AwsS3Exception extends AwsException {

	public AwsS3Exception(String message) {
		super(message);
	}

	public AwsS3Exception(String message, Throwable cause) {
		super(message, cause);
	}

}
