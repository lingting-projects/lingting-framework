package live.lingting.framework.dingtalk;

import com.fasterxml.jackson.annotation.JsonProperty;
import live.lingting.framework.jackson.JacksonUtils;

/**
 * 钉钉返回信息
 *
 * @author lingting 2020/6/11 0:23
 */

public class DingTalkResponse {

	public static final Long SUCCESS_CODE = 0L;

	@JsonProperty("errcode")
	private Long code;

	/**
	 * 值为ok表示无异常
	 */
	@JsonProperty("errmsg")
	private String message;

	/**
	 * 钉钉返回信息
	 */
	private String response;

	/**
	 * 是否发送成功
	 */
	private boolean success;

	public static DingTalkResponse of(String res) {
		DingTalkResponse value = JacksonUtils.toObj(res, DingTalkResponse.class);
		value.setResponse(res);
		value.setSuccess(SUCCESS_CODE.equals(value.code));
		return value;
	}

	@Override
	public String toString() {
		return response;
	}

	public Long getCode() {return this.code;}

	public String getMessage() {return this.message;}

	public String getResponse() {return this.response;}

	public boolean isSuccess() {return this.success;}

	@JsonProperty("errcode")
	public DingTalkResponse setCode(Long code) {
		this.code = code;
		return this;
	}

	@JsonProperty("errmsg")
	public DingTalkResponse setMessage(String message) {
		this.message = message;
		return this;
	}

	public DingTalkResponse setResponse(String response) {
		this.response = response;
		return this;
	}

	public DingTalkResponse setSuccess(boolean success) {
		this.success = success;
		return this;
	}
}
