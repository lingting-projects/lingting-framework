package live.lingting.framework.http;

import live.lingting.framework.util.ThreadUtils;

import javax.net.SocketFactory;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

/**
 * @author lingting 2024-09-02 15:28
 */
public abstract class HttpClient {

	protected CookieStore cookie;

	public static JavaHttpClient.Builder java() {
		return new JavaHttpClient.Builder();
	}

	public static OkHttpClient.Builder okhttp() {
		return new OkHttpClient.Builder();
	}

	public CookieStore cookie() {
		return cookie;
	}

	public abstract Object client();

	public abstract HttpResponse request(HttpRequest request) throws IOException;

	public abstract void request(HttpRequest request, ResponseCallback callback) throws IOException;

	public <T> T request(HttpRequest request, Class<T> cls) throws IOException {
		HttpResponse response = request(request);
		return response.convert(cls);
	}

	public HttpResponse get(URI uri) throws IOException {
		return request(HttpRequest.newBuilder().GET().uri(uri).build());
	}

	public abstract static class Builder<C extends HttpClient, B extends HttpClient.Builder<C, B>> {

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

		protected CookieStore cookie;

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

		public B cookie(CookieStore cookie) {
			this.cookie = cookie;
			return (B) this;
		}

		public B memoryCookie() {
			CookieManager manager = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
			return cookie(manager.getCookieStore());
		}

		protected <A> void nonNull(A a, Consumer<A> consumer) {
			if (a == null) {
				return;
			}
			consumer.accept(a);
		}

		public C build() {
			C c = doBuild();
			c.cookie = cookie;
			return c;
		}

		protected abstract C doBuild();

	}

}
