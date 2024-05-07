package live.lingting.framework.http;

import live.lingting.framework.http.java.JavaHttpDelegateClient;
import live.lingting.framework.http.okhttp.OkHttpDelegateClient;
import live.lingting.framework.jackson.JacksonUtils;
import live.lingting.framework.util.ThreadUtils;

import javax.net.SocketFactory;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

/**
 * @author lingting 2024-05-07 13:35
 */
@SuppressWarnings("unchecked")
public abstract class HttpDelegateClient<D> {

	public static OkHttpDelegateClient.Builder okhttp() {
		return new OkHttpDelegateClient.Builder();
	}

	public static JavaHttpDelegateClient.Builder java() {
		return new JavaHttpDelegateClient.Builder();
	}

	public abstract D client();

	public abstract <T> HttpResponse<T> request(HttpRequest request, HttpResponse.BodyHandler<T> handler)
			throws IOException;

	public abstract <T> void request(HttpRequest request, HttpResponse.BodyHandler<T> handler,
			ResponseCallback<T> callback) throws IOException;

	public <T> T request(HttpRequest request, Class<T> cls) throws IOException {
		HttpResponse<String> response = request(request, HttpResponse.BodyHandlers.ofString());
		String body = response.body();
		if (String.class.isAssignableFrom(cls)) {
			return (T) body;
		}
		return JacksonUtils.toObj(body, cls);
	}

	public <T> HttpResponse<T> get(URI uri, HttpResponse.BodyHandler<T> handler) throws IOException {
		return request(HttpRequest.newBuilder().GET().uri(uri).build(), handler);
	}

	public abstract static class Builder<D, C extends HttpDelegateClient<D>, B extends Builder<D, C, B>> {

		protected ExecutorService executor = ThreadUtils.executor();

		protected SocketFactory socketFactory;

		/**
		 * HostnameVerifier，用于HTTPS安全连接
		 */
		protected HostnameVerifier hostnameVerifier;

		/**
		 * SSLSocketFactory，用于HTTPS安全连接
		 */
		protected SSLSocketFactory sslSocketFactory;

		protected X509TrustManager trustManager;

		protected Duration callTimeout;

		protected Duration connectTimeout;

		protected Duration readTimeout;

		protected Duration writeTimeout;

		protected ProxySelector proxySelector;

		public B socketFactory(SocketFactory socketFactory) {
			this.socketFactory = socketFactory;
			return (B) this;
		}

		public B executor(ExecutorService executor) {
			this.executor = executor;
			return (B) this;
		}

		public B hostnameVerifier(HostnameVerifier hostnameVerifier) {
			this.hostnameVerifier = hostnameVerifier;
			return (B) this;
		}

		public B ssl(SSLSocketFactory ssf, X509TrustManager trustManager) {
			this.sslSocketFactory = ssf;
			this.trustManager = trustManager;
			return (B) this;
		}

		public B disableSsl() {
			SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
			X509TrustAllManager manager = X509TrustAllManager.INSTANCE;
			HostnameAllVerifier verifier = HostnameAllVerifier.INSTANCE;
			return ssl(factory, manager).hostnameVerifier(verifier);
		}

		public B callTimeout(Duration callTimeout) {
			this.callTimeout = callTimeout;
			return (B) this;
		}

		public B connectTimeout(Duration connectTimeout) {
			this.connectTimeout = connectTimeout;
			return (B) this;
		}

		public B readTimeout(Duration readTimeout) {
			this.readTimeout = readTimeout;
			return (B) this;
		}

		public B writeTimeout(Duration writeTimeout) {
			this.writeTimeout = writeTimeout;
			return (B) this;
		}

		/**
		 * 无限等待时间
		 */
		public B infiniteTimeout() {
			return timeout(Duration.ZERO, Duration.ZERO, Duration.ZERO, Duration.ZERO);
		}

		public B timeout(Duration connectTimeout, Duration readTimeout) {
			return connectTimeout(connectTimeout).readTimeout(readTimeout);
		}

		public B timeout(Duration callTimeout, Duration connectTimeout, Duration readTimeout, Duration writeTimeout) {
			return callTimeout(callTimeout).connectTimeout(connectTimeout)
				.readTimeout(readTimeout)
				.writeTimeout(writeTimeout);
		}

		public B proxySelector(ProxySelector proxySelector) {
			this.proxySelector = proxySelector;
			return (B) this;
		}

		protected <A> void nonNull(A a, Consumer<A> consumer) {
			if (a == null) {
				return;
			}
			consumer.accept(a);
		}

		public abstract C build();

	}

}
