package live.lingting.framework.http.api;

import live.lingting.framework.http.HttpMethod;
import live.lingting.framework.http.body.BodySource;

/**
 * @author lingting 2024-09-19 15:41
 */
public class ApiSimpleRequest extends ApiRequest {

	protected final HttpMethod method;

	protected final String uri;

	protected final BodySource body;

	public ApiSimpleRequest(HttpMethod method, String uri) {
		this(method, uri, null);
	}

	public ApiSimpleRequest(HttpMethod method, String uri, BodySource body) {
		this.method = method;
		this.uri = uri;
		this.body = body;
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
	public BodySource body() {
		return body;
	}

}
