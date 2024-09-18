package live.lingting.framework.ali;

import live.lingting.framework.http.api.ApiRequest;

/**
 * @author lingting 2024-09-14 13:49
 */
public abstract class AliRequest extends ApiRequest {


	public String contentType() {
		return "application/json;charset=utf8";
	}


}
