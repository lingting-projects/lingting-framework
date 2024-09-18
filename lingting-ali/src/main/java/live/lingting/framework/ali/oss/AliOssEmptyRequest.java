package live.lingting.framework.ali.oss;

import live.lingting.framework.http.HttpMethod;
import live.lingting.framework.http.HttpUrlBuilder;
import live.lingting.framework.value.multi.StringMultiValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.net.http.HttpRequest;
import java.util.function.Consumer;

/**
 * @author lingting 2024-09-13 16:46
 */
@Getter
@RequiredArgsConstructor
public class AliOssEmptyRequest extends AliOssRequest {

	protected final HttpMethod method;

	protected final StringMultiValue params = new StringMultiValue();

	@Override
	public HttpMethod method() {
		return method;
	}

	@Override
	public void configure(HttpUrlBuilder builder) {
		super.configure(builder);
		builder.addParams(params);
	}

	public AliOssEmptyRequest configure(Consumer<StringMultiValue> consumer) {
		consumer.accept(params);
		return this;
	}

	@Override
	public HttpRequest.BodyPublisher body() {
		return HttpRequest.BodyPublishers.noBody();
	}

}
