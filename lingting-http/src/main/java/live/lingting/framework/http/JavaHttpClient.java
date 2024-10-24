package live.lingting.framework.http;

import live.lingting.framework.http.body.BodySource;
import live.lingting.framework.http.body.MemoryBody;
import live.lingting.framework.http.header.HttpHeaders;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author lingting 2024-09-02 15:33
 */
@RequiredArgsConstructor
public class JavaHttpClient extends HttpClient {

	protected final java.net.http.HttpClient client;

	public static HttpResponse convert(HttpRequest request, java.net.http.HttpResponse<InputStream> r) {
		int code = r.statusCode();
		Map<String, List<String>> map = r.headers().map();
		HttpHeaders headers = HttpHeaders.of(map);
		InputStream body = r.body();
		return new HttpResponse(request, code, headers, body);
	}

	public static java.net.http.HttpRequest convert(HttpRequest request) {
		HttpMethod method = request.method();
		URI uri = request.uri();
		HttpHeaders headers = request.headers();
		HttpRequest.Body body = request.body();

		java.net.http.HttpRequest.Builder builder = java.net.http.HttpRequest.newBuilder().uri(uri);
		java.net.http.HttpRequest.BodyPublisher publisher = convert(method, body);
		builder.method(method.name(), publisher);
		headers.each((k, v) -> {
			if (HEADERS_DISABLED.contains(k)) {
				return;
			}
			builder.header(k, v);
		});
		return builder.build();
	}

	public static java.net.http.HttpRequest.BodyPublisher convert(HttpMethod method, HttpRequest.Body body) {
		if (body == null || !method.allowBody()) {
			return BodyPublishers.noBody();
		}
		BodySource source = body.source();
		if (source instanceof MemoryBody) {
			return BodyPublishers.ofByteArray(source.bytes());
		}
		return BodyPublishers.ofInputStream(source::openInput);
	}

	@Override
	public java.net.http.HttpClient client() {
		return client;
	}

	@SneakyThrows
	@Override
	public HttpResponse request(HttpRequest request) throws IOException {
		java.net.http.HttpRequest jr = convert(request);
		java.net.http.HttpResponse<InputStream> r = client.send(jr, BodyHandlers.ofInputStream());
		return convert(request, r);
	}

	@Override
	public void request(HttpRequest request, ResponseCallback callback) {
		java.net.http.HttpRequest jr = convert(request);
		client.sendAsync(jr, BodyHandlers.ofInputStream()).whenComplete((r, throwable) -> {
			try {
				HttpResponse response = convert(request, r);

				if (throwable != null) {
					callback.onError(request, throwable);
				}
				else {
					callback.onResponse(response);
				}
			}
			catch (Throwable e) {
				callback.onError(request, e);
			}
		});
	}

	public static class Builder extends HttpClient.Builder<JavaHttpClient, JavaHttpClient.Builder> {

		protected Authenticator authenticator;

		public Builder authenticator(Authenticator authenticator) {
			this.authenticator = authenticator;
			return this;
		}

		@Override
		public Builder infiniteTimeout() {
			return timeout(null, null, null, null);
		}

		@SneakyThrows
		public JavaHttpClient build(Supplier<java.net.http.HttpClient.Builder> supplier) {
			java.net.http.HttpClient.Builder builder = supplier.get();

			if (trustManager != null) {
				SSLContext context = Https.sslContext(trustManager);
				builder.sslContext(context);
			}

			nonNull(connectTimeout, builder::connectTimeout);
			nonNull(proxySelector, builder::proxy);
			nonNull(executor, builder::executor);
			nonNull(authenticator, builder::authenticator);

			builder.followRedirects(redirects ? Redirect.ALWAYS : Redirect.NEVER);

			if (cookie != null) {
				builder.cookieHandler(new CookieManager(cookie, CookiePolicy.ACCEPT_ALL));
			}

			java.net.http.HttpClient delegate = builder.build();
			return new JavaHttpClient(delegate);
		}

		@Override
		protected JavaHttpClient doBuild() {
			return build(java.net.http.HttpClient::newBuilder);
		}

	}

}
