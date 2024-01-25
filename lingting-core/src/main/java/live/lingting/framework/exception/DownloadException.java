package live.lingting.framework.exception;

/**
 * @author lingting 2024-01-16 19:41
 */
public class DownloadException extends RuntimeException {

	public DownloadException(String message) {
		super(message);
	}

	public DownloadException(String message, Throwable cause) {
		super(message, cause);
	}

}
