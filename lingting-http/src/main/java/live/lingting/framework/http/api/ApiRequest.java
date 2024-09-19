package live.lingting.framework.http.api;

import live.lingting.framework.http.HttpMethod;
import live.lingting.framework.http.header.HttpHeaders;
import live.lingting.framework.jackson.JacksonUtils;
import live.lingting.framework.value.multi.StringMultiValue;
import lombok.Getter;

import java.net.http.HttpRequest;

/**
 * @author lingting 2024-09-14 15:33
 */
@Getter
public abstract class ApiRequest {

	protected final HttpHeaders headers = HttpHeaders.empty();

	protected final StringMultiValue params = new StringMultiValue();

	public abstract HttpMethod method();

	public abstract String path();

	public HttpRequest.BodyPublisher body() {
		String json = JacksonUtils.toJson(this);
		return HttpRequest.BodyPublishers.ofString(json);
	}

	/**
	 * 在发起请求前触发
	 */
	public void onCall() {
		//
	}

	public void onParams() {
		//
	}

}
