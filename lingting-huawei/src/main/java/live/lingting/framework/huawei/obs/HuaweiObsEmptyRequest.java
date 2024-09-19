package live.lingting.framework.huawei.obs;

import live.lingting.framework.http.HttpMethod;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.net.http.HttpRequest;

/**
 * @author lingting 2024-09-13 16:46
 */
@Getter
@RequiredArgsConstructor
public class HuaweiObsEmptyRequest extends HuaweiObsRequest {

	protected final HttpMethod method;

	@Override
	public HttpMethod method() {
		return method;
	}

	@Override
	public HttpRequest.BodyPublisher body() {
		return HttpRequest.BodyPublishers.noBody();
	}

}
