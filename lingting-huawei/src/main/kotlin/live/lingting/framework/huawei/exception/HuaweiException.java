package live.lingting.framework.huawei.exception;

/**
 * @author lingting 2024-09-12 21:40
 */
public class HuaweiException extends RuntimeException {

	public HuaweiException(String message) {
		super(message);
	}

	public HuaweiException(String message, Throwable cause) {
		super(message, cause);
	}

}
