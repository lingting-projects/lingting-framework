package live.lingting.framework.huawei;

import live.lingting.framework.http.HttpClient;
import live.lingting.framework.http.HttpMethod;
import live.lingting.framework.http.HttpResponse;
import live.lingting.framework.http.HttpUrlBuilder;
import live.lingting.framework.http.header.HttpHeaders;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;

import java.net.URI;
import java.net.http.HttpRequest;

import static live.lingting.framework.huawei.HuaweiUtils.CLIENT;

/**
 * @author lingting 2024-09-14 15:07
 */
@RequiredArgsConstructor
public abstract class HuaweiClient<R extends HuaweiRequest> {

	protected final String host;

	@Setter
	protected HttpClient client = CLIENT;

	protected void configure(R request) {
		//
	}

	protected void configure(R request, HttpHeaders headers) {
		//
	}

	protected void configure(R request, HttpHeaders headers, HttpUrlBuilder urlBuilder) {
		//
	}

	protected abstract HttpResponse checkout(R request, HttpResponse response);

	@SneakyThrows
	protected HttpResponse call(R r) {
		HttpHeaders headers = HttpHeaders.empty();

		String contentType = r.contentType();
		HttpMethod method = r.method();
		HttpRequest.BodyPublisher body = r.body();

		headers.contentType(contentType);

		HttpUrlBuilder urlBuilder = HttpUrlBuilder.builder().https().host(host);

		configure(r);
		configure(r, headers);
		r.configure(urlBuilder);
		configure(r, headers, urlBuilder);
		URI uri = urlBuilder.buildUri();

		HttpRequest.Builder builder = HttpRequest.newBuilder().method(method.name(), body);
		headers.each(builder::header);
		HttpRequest request = builder.uri(uri).build();
		HttpResponse response = client.request(request);
		return checkout(r, response);
	}

}
