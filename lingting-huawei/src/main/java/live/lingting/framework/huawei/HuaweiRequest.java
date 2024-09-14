package live.lingting.framework.huawei;

import live.lingting.framework.http.api.ApiRequest;
import lombok.Getter;

/**
 * @author lingting 2024-09-12 21:43
 */
@Getter
public abstract class HuaweiRequest extends ApiRequest {

	public String contentType() {
		return "application/json;charset=utf8";
	}

}
