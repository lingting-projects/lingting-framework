package live.lingting.framework.http.java;

import live.lingting.framework.http.HttpDelegateClient;
import live.lingting.framework.http.ResponseCallback;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.IOException;
import java.net.Authenticator;
import java.net.CookieHandler;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.SecureRandom;

/**
 * @author lingting 2024-05-07 13:52
 */
@RequiredArgsConstructor
public class JavaHttpDelegateClient extends HttpDelegateClient<HttpClient> {

	private final HttpClient client;

	@Override
	public HttpClient client() {
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

	public static class Builder extends HttpDelegateClient.Builder<HttpClient, JavaHttpDelegateClient, Builder> {

		protected Authenticator authenticator;

		protected CookieHandler cookieHandler;

		public Builder authenticator(Authenticator authenticator) {
			this.authenticator = authenticator;
			return this;
		}

		public Builder cookie(CookieHandler cookieHandler) {
			this.cookieHandler = cookieHandler;
			return this;
		}

		@Override
		public Builder infiniteTimeout() {
			return timeout(null, null, null, null);
		}

		@SneakyThrows
		@Override
		public JavaHttpDelegateClient build() {
			HttpClient.Builder builder = HttpClient.newBuilder();

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
			nonNull(cookieHandler, builder::cookieHandler);

			HttpClient delegate = builder.build();
			return new JavaHttpDelegateClient(delegate);
		}

	}

}
