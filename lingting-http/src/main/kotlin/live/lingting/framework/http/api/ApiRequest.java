package live.lingting.framework.http.api;

import live.lingting.framework.http.HttpMethod;
import live.lingting.framework.http.body.BodySource;
import live.lingting.framework.http.body.MemoryBody;
import live.lingting.framework.http.header.HttpHeaders;
import live.lingting.framework.jackson.JacksonUtils;
import live.lingting.framework.value.multi.StringMultiValue;

/**
 * @author lingting 2024-09-14 15:33
 */
public abstract class ApiRequest {

	protected final HttpHeaders headers = HttpHeaders.empty();

	protected final StringMultiValue params = new StringMultiValue();

	public abstract HttpMethod method();

	public abstract String path();

	public BodySource body() {
		String json = JacksonUtils.toJson(this);
		return new MemoryBody(json);
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

	public HttpHeaders getHeaders() {return this.headers;}

	public StringMultiValue getParams() {return this.params;}
}
