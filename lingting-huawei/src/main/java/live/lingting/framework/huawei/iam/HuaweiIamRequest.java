package live.lingting.framework.huawei.iam;

import live.lingting.framework.http.HttpMethod;
import live.lingting.framework.http.HttpUrlBuilder;
import live.lingting.framework.http.header.HttpHeaders;
import live.lingting.framework.jackson.JacksonUtils;
import lombok.Getter;

import java.net.http.HttpRequest;

/**
 * @author lingting 2024-09-12 21:43
 */
public abstract class HuaweiIamRequest {

	@Getter
	protected HttpHeaders headers = HttpHeaders.empty();

	public boolean usingToken() {
		return true;
	}

	public String contentType() {
		return "application/json;charset=utf8";
	}

	public HttpMethod method() {
		return HttpMethod.POST;
	}

	public HttpUrlBuilder urlBuilder() {
		HttpUrlBuilder builder = HttpUrlBuilder.builder();
		configure(builder);
		return builder;
	}

	public abstract void configure(HttpUrlBuilder builder);

	public HttpRequest.Builder builder() {
		String contentType = contentType();
		HttpMethod method = method();
		HttpRequest.BodyPublisher body = body();

		HttpRequest.Builder builder = HttpRequest.newBuilder()
			.setHeader("Content-Type", contentType)
			.method(method.name(), body);
		configure(builder);
		return builder;
	}

	public HttpRequest.BodyPublisher body() {
		String json = JacksonUtils.toJson(this);
		return HttpRequest.BodyPublishers.ofString(json);
	}

	public void configure(HttpRequest.Builder builder) {
	}

}
