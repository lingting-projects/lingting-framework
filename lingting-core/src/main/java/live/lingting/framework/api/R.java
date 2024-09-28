package live.lingting.framework.api;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author lingting 2024-01-25 11:12
 */
@Getter
@ToString
@EqualsAndHashCode
@SuppressWarnings("java:S1948")
public final class R<T> implements Serializable {

	private static final long serialVersionUID = 20240125;

	private final Integer code;

	private final T data;

	private final String message;

	/**
	*/
	public R(Integer code, T data, String message) {
		this.code = code;
		this.data = data;
		this.message = message;
	}

	public static <T> R<T> of(int code, String message) {
		return of(code, message, null);
	}

	public static <T> R<T> of(int code, String message, T data) {
		return new R<>(code, data, message);
	}

	public static <T> R<T> ok() {
		return ok(null);
	}

	public static <T> R<T> ok(T data) {
		return ok(ApiResultCode.SUCCESS, data);
	}

	public static <T> R<T> ok(ResultCode code, T data) {
		return of(code.getCode(), data, code.getMessage());
	}

	public static <T> R<T> failed(ResultCode code) {
		return of(code.getCode(), code.getMessage());
	}

	public static <T> R<T> failed(ResultCode code, String message) {
		return of(code.getCode(), message);
	}

	public static <T> R<T> failed(Integer code, String message) {
		return of(code, null, message);
	}

	public static <T> R<T> of(Integer code, T data, String message) {
		return new R<>(code, data, message);
	}

	public Integer code() {
		return code;
	}

	public T data() {
		return data;
	}

	public String message() {
		return message;
	}

}
