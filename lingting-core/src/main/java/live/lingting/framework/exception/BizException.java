package live.lingting.framework.exception;

import live.lingting.framework.api.ResultCode;
import lombok.Getter;

/**
 * @author lingting 2022/9/22 12:11
 */
@Getter
public class BizException extends RuntimeException {

	private final Integer code;

	private final String message;

	public BizException(ResultCode resultCode) {
		this(resultCode.getCode(), resultCode.getMessage(), null);
	}

	public BizException(ResultCode resultCode, Exception e) {
		this(resultCode, resultCode.getMessage(), e);
	}

	public BizException(ResultCode resultCode, String message, Exception e) {
		this(resultCode.getCode(), message, e);
	}

	public BizException(Integer code, String message) {
		this(code, message, null);
	}

	public BizException(Integer code, String message, Exception e) {
		super(message, e);
		this.message = message;
		this.code = code;
	}

}
