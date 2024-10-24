package live.lingting.framework.http;

import live.lingting.framework.stream.BytesInputStream;
import live.lingting.framework.stream.FileCloneInputStream;
import live.lingting.framework.util.FileUtils;
import live.lingting.framework.util.StreamUtils;
import live.lingting.framework.util.ThreadUtils;
import live.lingting.framework.value.LazyValue;
import lombok.SneakyThrows;

import javax.net.SocketFactory;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.ProxySelector;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * @author lingting 2024-09-02 15:28
 */
public abstract class HttpClient {

	/**
	 * @see jdk.internal.net.http.common.Utils#getDisallowedHeaders()
	 */
	protected static final Set<String> HEADERS_DISABLED = Set.of("connection", "content-length", "expect", "host",
			"upgrade");

	public static final File TEMP_DIR = FileUtils.createTempDir("http");

	/**
	 * 默认大小以内文件直接放内存里面
	 */
	public static final long MAX_BYTES = 5242880;

	protected CookieStore cookie;

	public static JavaHttpClient.Builder java() {
		return new JavaHttpClient.Builder();
	}

	public static OkHttpClient.Builder okhttp() {
		return new OkHttpClient.Builder();
	}

	public static InputStream wrap(InputStream source) throws IOException {
		return wrap(source, MAX_BYTES);
	}

	public static InputStream wrap(InputStream source, long maxBytes) throws IOException {
		if (source == null) {
			return new ByteArrayInputStream(new byte[0]);
		}
		AtomicLong size = new AtomicLong(0);

		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		LazyValue<FileOutputStream> fileOutValue = new LazyValue<>(() -> null);
		LazyValue<File> fileValue = new LazyValue<>(() -> {
			File file = FileUtils.createTemp(".wrap", TEMP_DIR);
			fileOutValue.set(new FileOutputStream(file));
			return file;
		});
		StreamUtils.read(source, (bytes, len) -> {
			if (!fileValue.isFirst() || size.addAndGet(len) > maxBytes) {
				if (fileValue.isFirst()) {
					fileValue.get();
					fileOutValue.get().write(byteOut.toByteArray());
				}
				fileOutValue.get().write(bytes, 0, len);
			}
			else {
				byteOut.write(bytes, 0, len);
			}
		});
		if (fileValue.isFirst()) {
			return new BytesInputStream(byteOut.toByteArray());
		}
		return new FileCloneInputStream(fileValue.get());
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
		return request(HttpRequest.builder().get().url(uri).build());
	}

	public abstract static class Builder<C extends HttpClient, B extends Builder<C, B>> {

		protected ExecutorService executor = ThreadUtils.executor();

		protected boolean redirects = true;

		protected SocketFactory socketFactory;

		/**
		 * HostnameVerifier，用于HTTPS安全连接
		 */
		protected HostnameVerifier hostnameVerifier;

		/**
		 * 用于HTTPS安全连接
		 */
		protected SSLContext sslContext;

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

		public B redirects(boolean redirects) {
			this.redirects = redirects;
			return (B) this;
		}

		public B hostnameVerifier(HostnameVerifier hostnameVerifier) {
			this.hostnameVerifier = hostnameVerifier;
			return (B) this;
		}

		public B ssl(X509TrustManager trustManager) throws NoSuchAlgorithmException, KeyManagementException {
			SSLContext context = Https.sslContext(trustManager);
			return ssl(context, trustManager);
		}

		public B ssl(SSLContext context, X509TrustManager trustManager) {
			this.sslContext = context;
			this.trustManager = trustManager;
			return (B) this;
		}

		@SneakyThrows
		public B disableSsl() {
			X509TrustManager manager = Https.SSL_DISABLED_TRUST_MANAGER;
			HostnameVerifier verifier = Https.SSL_DISABLED_HOSTNAME_VERIFIER;
			return ssl(manager).hostnameVerifier(verifier);
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
