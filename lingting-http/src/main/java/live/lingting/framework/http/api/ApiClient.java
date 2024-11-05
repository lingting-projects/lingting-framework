package live.lingting.framework.http.api;

import live.lingting.framework.http.HttpClient;
import live.lingting.framework.http.HttpMethod;
import live.lingting.framework.http.HttpRequest;
import live.lingting.framework.http.HttpResponse;
import live.lingting.framework.http.HttpUrlBuilder;
import live.lingting.framework.http.body.BodySource;
import live.lingting.framework.http.header.HttpHeaders;
import live.lingting.framework.value.multi.StringMultiValue;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.slf4j.Logger;

import java.net.URI;
import java.time.Duration;

/**
 * @author lingting 2024-09-14 15:33
 */
public abstract class ApiClient<R extends ApiRequest> {

	@Setter
	static HttpClient defaultClient = HttpClient.okhttp()
		.disableSsl()
		.timeout(Duration.ofSeconds(15), Duration.ofSeconds(30))
		.build();

	@Getter
	protected final Logger log = org.slf4j.LoggerFactory.getLogger(getClass());

	protected final String host;

	@Setter
	protected HttpClient client = defaultClient;

	protected ApiClient(String host) {
		this.host = host;
	}

	protected void customize(R request) {
		//
	}

	protected void customize(HttpHeaders headers) {
		//
	}

	protected void customize(R request, HttpHeaders headers) {
		//
	}

	protected void customize(HttpUrlBuilder builder) {
		//
	}

	protected void customize(R request, HttpRequest.Builder builder) {
		//
	}

	protected void customize(R request, HttpHeaders headers, BodySource source, StringMultiValue params) {
		//
	}

	protected abstract HttpResponse checkout(R request, HttpResponse response);

	@SneakyThrows
	protected HttpResponse call(R r) {
		r.onCall();
		customize(r);

		HttpMethod method = r.method();
		HttpHeaders headers = HttpHeaders.of(r.getHeaders());
		BodySource body = r.body();

		customize(headers);
		customize(r, headers);

		String path = r.path();
		r.onParams();
		HttpUrlBuilder urlBuilder = HttpUrlBuilder.builder().https().host(host).uri(path).addParams(r.getParams());
		customize(urlBuilder);

		HttpRequest.Builder builder = HttpRequest.builder();
		URI uri = urlBuilder.buildUri();
		builder.url(uri);
		headers.host(uri.getHost());

		customize(r, builder);
		customize(r, headers, body, urlBuilder.params());
		builder.headers(headers);
		builder.method(method.name()).body(body);

		HttpRequest request = builder.build();
		HttpResponse response = client.request(request);
		return checkout(r, response);
	}

}
