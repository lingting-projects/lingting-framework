package live.lingting.framework.http.api;

import live.lingting.framework.http.HttpMethod;

import java.net.http.HttpRequest;

/**
 * @author lingting 2024-09-19 15:41
 */
public class ApiSimpleRequest extends ApiRequest {

	protected final HttpMethod method;

	protected final String uri;

	protected final HttpRequest.BodyPublisher body;

	public ApiSimpleRequest(HttpMethod method, String uri) {
		this(method, uri, null);
	}

	public ApiSimpleRequest(HttpMethod method, String uri, HttpRequest.BodyPublisher body) {
		this.method = method;
		this.uri = uri;
		this.body = body != null ? body : HttpRequest.BodyPublishers.noBody();
	}

	@Override
	public HttpMethod method() {
		return method;
	}

	@Override
	public String path() {
		return uri;
	}

	@Override
	public HttpRequest.BodyPublisher body() {
		return body;
	}

}
