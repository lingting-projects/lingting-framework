package live.lingting.framework.http;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.IOException;
import java.net.Authenticator;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.SecureRandom;
import java.util.function.Supplier;

/**
 * @author lingting 2024-09-02 15:33
 */
@RequiredArgsConstructor
public class JavaHttpClient extends HttpClient {

	protected final java.net.http.HttpClient client;

	@Override
	public java.net.http.HttpClient client() {
		return client;
	}

	@SneakyThrows
	@Override
	public <T> HttpResponse<T> request(HttpRequest request, HttpResponse.BodyHandler<T> handler) throws IOException {
		return client.send(request, handler);
	}

	@Override
	public <T> void request(HttpRequest request, HttpResponse.BodyHandler<T> handler, ResponseCallback<T> callback) {
		client.sendAsync(request, handler).whenComplete((response, throwable) -> {
			try {
				if (throwable != null) {
					callback.onError(request, throwable);
				}
				else {
					callback.onResponse(request, response);
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
				SSLContext context = SSLContext.getInstance("TLS");
				SecureRandom random = new SecureRandom();
				context.init(null, new TrustManager[] { trustManager }, random);
				builder.sslContext(context);
			}

			nonNull(connectTimeout, builder::connectTimeout);
			nonNull(proxySelector, builder::proxy);
			nonNull(executor, builder::executor);
			nonNull(authenticator, builder::authenticator);

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
