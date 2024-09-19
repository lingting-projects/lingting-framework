package live.lingting.framework.ali;

import live.lingting.framework.http.HttpMethod;
import live.lingting.framework.http.api.ApiRequest;

/**
 * @author lingting 2024-09-14 13:49
 */
public abstract class AliRequest extends ApiRequest {
	@Override
	public HttpMethod method() {
		return HttpMethod.POST;
	}

	@Override
	public void onCall() {
		headers.contentType("application/json;charset=utf8");
	}

}
