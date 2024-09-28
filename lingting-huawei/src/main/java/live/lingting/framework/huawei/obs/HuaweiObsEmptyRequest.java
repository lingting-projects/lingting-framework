package live.lingting.framework.huawei.obs;

import live.lingting.framework.http.HttpMethod;
import live.lingting.framework.http.body.BodySource;
import live.lingting.framework.http.body.MemoryBody;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

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
	public MemoryBody body() {
		return BodySource.empty();
	}

}
