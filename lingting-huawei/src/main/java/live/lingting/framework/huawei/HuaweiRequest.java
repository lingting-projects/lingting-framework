package live.lingting.framework.huawei;

import live.lingting.framework.http.HttpMethod;
import live.lingting.framework.http.api.ApiRequest;
import lombok.Getter;

/**
 * @author lingting 2024-09-12 21:43
 */
@Getter
public abstract class HuaweiRequest extends ApiRequest {

	@Override
	public HttpMethod method() {
		return HttpMethod.POST;
	}

	@Override
	public void onCall() {
		headers.contentType("application/json;charset=utf8");
	}

}
