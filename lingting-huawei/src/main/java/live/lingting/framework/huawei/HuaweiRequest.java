package live.lingting.framework.huawei;

import live.lingting.framework.http.HttpMethod;
import live.lingting.framework.http.HttpUrlBuilder;
import live.lingting.framework.jackson.JacksonUtils;
import lombok.Getter;

import java.net.http.HttpRequest;

/**
 * @author lingting 2024-09-12 21:43
 */
@Getter
public abstract class HuaweiRequest {

	public String contentType() {
		return "application/json;charset=utf8";
	}

	public HttpMethod method() {
		return HttpMethod.POST;
	}

	public abstract void configure(HttpUrlBuilder builder);

	public HttpRequest.BodyPublisher body() {
		String json = JacksonUtils.toJson(this);
		return HttpRequest.BodyPublishers.ofString(json);
	}

	public void configure(HttpRequest.Builder builder) {
	}

}
