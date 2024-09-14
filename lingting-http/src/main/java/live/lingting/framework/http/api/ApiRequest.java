package live.lingting.framework.http.api;

import live.lingting.framework.http.HttpMethod;
import live.lingting.framework.http.HttpUrlBuilder;
import live.lingting.framework.jackson.JacksonUtils;

import java.net.http.HttpRequest;

/**
 * @author lingting 2024-09-14 15:33
 */
public abstract class ApiRequest {

	public abstract String contentType();

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
