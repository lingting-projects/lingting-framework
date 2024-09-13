package live.lingting.framework.huawei.obs;

import live.lingting.framework.http.HttpMethod;
import live.lingting.framework.http.HttpUrlBuilder;
import live.lingting.framework.value.multi.ListMultiValue;
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

	protected final ListMultiValue<String, String> params = new ListMultiValue<>();

	@Override
	public HttpMethod method() {
		return method;
	}

	@Override
	public void configure(HttpUrlBuilder builder) {
		super.configure(builder);
		builder.addParams(params);
	}

	@Override
	public HttpRequest.BodyPublisher body() {
		return HttpRequest.BodyPublishers.noBody();
	}

}
