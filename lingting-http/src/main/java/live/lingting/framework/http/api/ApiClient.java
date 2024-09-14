package live.lingting.framework.http.api;

import live.lingting.framework.http.HttpClient;
import live.lingting.framework.http.HttpMethod;
import live.lingting.framework.http.HttpResponse;
import live.lingting.framework.http.HttpUrlBuilder;
import live.lingting.framework.http.header.HttpHeaders;
import lombok.Setter;
import lombok.SneakyThrows;
import org.slf4j.Logger;

import java.net.URI;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.Set;

/**
 * @author lingting 2024-09-14 15:33
 */
public abstract class ApiClient<R extends ApiRequest> {

	public static final HttpClient DEFAULT_CLIENT = HttpClient.okhttp()
		.disableSsl()
		.timeout(Duration.ofSeconds(15), Duration.ofSeconds(30))
		.build();

	/**
	 * @see jdk.internal.net.http.common.Utils#getDisallowedHeaders()
	 */
	protected static final Set<String> HEADERS_DISABLED = Set.of("connection", "content-length", "expect", "host",
			"upgrade");

	protected final Logger log = org.slf4j.LoggerFactory.getLogger(getClass());

	protected final String host;

	@Setter
	protected HttpClient client = DEFAULT_CLIENT;

	protected ApiClient(String host) {
		this.host = host;
	}

	protected void configure(R request) {
		//
	}

	protected void configure(HttpHeaders headers) {
		//
	}

	protected void configure(R request, HttpHeaders headers) {
		//
	}

	protected void configure(HttpUrlBuilder builder) {
		//
	}

	protected void configure(R request, HttpHeaders headers, HttpUrlBuilder urlBuilder) {
		//
	}

	protected void configure(R request, HttpHeaders headers, HttpRequest.Builder builder, HttpUrlBuilder urlBuilder) {
		//
	}

	protected abstract HttpResponse checkout(R request, HttpResponse response);

	@SneakyThrows
	protected HttpResponse call(R r) {
		HttpRequest.Builder builder = HttpRequest.newBuilder();
		configure(r);

		String contentType = r.contentType();
		HttpMethod method = r.method();
		HttpRequest.BodyPublisher body = r.body();

		HttpHeaders headers = HttpHeaders.empty().contentType(contentType);
		configure(headers);
		configure(r, headers);

		HttpUrlBuilder urlBuilder = HttpUrlBuilder.builder().https().host(host);
		configure(urlBuilder);
		r.configure(urlBuilder);
		configure(r, headers, urlBuilder);

		URI uri = urlBuilder.buildUri();
		builder.uri(uri);
		headers.host(uri.getHost());

		builder.method(method.name(), body);
		configure(r, headers, builder, urlBuilder);
		headers.each((k, v) -> {
			if (HEADERS_DISABLED.contains(k)) {
				return;
			}
			builder.header(k, v);
		});
		r.configure(builder);

		HttpRequest request = builder.build();
		HttpResponse response = client.request(request);
		return checkout(r, response);
	}

}
