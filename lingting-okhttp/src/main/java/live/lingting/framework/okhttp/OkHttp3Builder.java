package live.lingting.framework.okhttp;

import live.lingting.framework.http.HostnameAllVerifier;
import live.lingting.framework.http.X509TrustAllManager;
import lombok.Getter;
import okhttp3.CookieJar;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;

import javax.net.SocketFactory;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import java.net.Proxy;
import java.net.ProxySelector;
import java.time.Duration;
import java.util.function.UnaryOperator;

/**
 * @author lingting 2023/2/2 17:26
 */
@Getter
@SuppressWarnings("java:S1700")
public class OkHttp3Builder {

	private OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();

	private SocketFactory socketFactory;

	/**
	 * HostnameVerifier，用于HTTPS安全连接
	 */
	private HostnameVerifier hostnameVerifier;

	/**
	 * SSLSocketFactory，用于HTTPS安全连接
	 */
	private SSLSocketFactory sslSocketFactory;

	private X509TrustManager trustManager;

	private Duration callTimeout;

	private Duration connectTimeout;

	private Duration readTimeout;

	private Duration writeTimeout;

	private CookieJar cookieJar;

	private Proxy proxy;

	private ProxySelector proxySelector;

	private Dispatcher dispatcher;

	public static OkHttp3Builder builder() {
		return new OkHttp3Builder();
	}

	public OkHttpClient.Builder builder(OkHttpClient.Builder builder) {
		if (builder == null) {
			builder = new OkHttpClient.Builder();
		}

		if (socketFactory != null) {
			builder.socketFactory(socketFactory);
		}

		if (hostnameVerifier != null) {
			builder.hostnameVerifier(hostnameVerifier);
		}

		if (sslSocketFactory != null) {
			builder.sslSocketFactory(sslSocketFactory, trustManager);
		}

		if (callTimeout != null) {
			builder.callTimeout(callTimeout);
		}

		if (connectTimeout != null) {
			builder.connectTimeout(connectTimeout);
		}

		if (readTimeout != null) {
			builder.readTimeout(readTimeout);
		}

		if (writeTimeout != null) {
			builder.writeTimeout(writeTimeout);
		}

		if (cookieJar != null) {
			builder.setCookieJar$okhttp(cookieJar);
		}

		if (proxy != null) {
			builder.proxy(proxy);
		}

		if (proxySelector != null) {
			builder.proxySelector(proxySelector);
		}

		if (dispatcher != null) {
			builder.dispatcher(dispatcher);
		}

		return builder;
	}

	public OkHttp3 build() {
		return build(okHttpClientBuilder);
	}

	public OkHttp3 build(OkHttpClient.Builder clientBuilder) {
		return new OkHttp3(builder(clientBuilder).build());
	}

	public OkHttp3 build(UnaryOperator<OkHttpClient.Builder> operator) {
		OkHttpClient.Builder build = builder(okHttpClientBuilder);
		return new OkHttp3(operator.apply(build).build());
	}

	public OkHttp3Builder okHttpClientBuilder(OkHttpClient.Builder httpClientBuilder) {
		this.okHttpClientBuilder = httpClientBuilder;
		return this;
	}

	public OkHttp3Builder socketFactory(SocketFactory socketFactory) {
		this.socketFactory = socketFactory;
		return this;
	}

	public OkHttp3Builder hostnameVerifier(HostnameVerifier hostnameVerifier) {
		this.hostnameVerifier = hostnameVerifier;
		return this;
	}

	public OkHttp3Builder ssl(SSLSocketFactory ssf, X509TrustManager trustManager) {
		this.sslSocketFactory = ssf;
		this.trustManager = trustManager;
		return this;
	}

	public OkHttp3Builder disableSsl() {
		return ssl((SSLSocketFactory) SSLSocketFactory.getDefault(), X509TrustAllManager.INSTANCE)
			.hostnameVerifier(new HostnameAllVerifier());
	}

	public OkHttp3Builder callTimeout(Duration callTimeout) {
		this.callTimeout = callTimeout;
		return this;
	}

	public OkHttp3Builder connectTimeout(Duration connectTimeout) {
		this.connectTimeout = connectTimeout;
		return this;
	}

	public OkHttp3Builder readTimeout(Duration readTimeout) {
		this.readTimeout = readTimeout;
		return this;
	}

	public OkHttp3Builder writeTimeout(Duration writeTimeout) {
		this.writeTimeout = writeTimeout;
		return this;
	}

	/**
	 * 无限等待时间
	 */
	public OkHttp3Builder infiniteTimeout() {
		return timeout(Duration.ZERO, Duration.ZERO, Duration.ZERO, Duration.ZERO);
	}

	public OkHttp3Builder timeout(Duration connectTimeout, Duration readTimeout) {
		return connectTimeout(connectTimeout).readTimeout(readTimeout);
	}

	public OkHttp3Builder timeout(Duration callTimeout, Duration connectTimeout, Duration readTimeout,
			Duration writeTimeout) {
		return callTimeout(callTimeout).connectTimeout(connectTimeout)
			.readTimeout(readTimeout)
			.writeTimeout(writeTimeout);
	}

	public OkHttp3Builder cookieJar(CookieJar jar) {
		this.cookieJar = jar;
		return this;
	}

	public OkHttp3Builder keepCookieJar() {
		return cookieJar(new KeepCookieJar());
	}

	public OkHttp3Builder proxy(Proxy proxy) {
		this.proxy = proxy;
		return this;
	}

	public OkHttp3Builder proxySelector(ProxySelector proxySelector) {
		this.proxySelector = proxySelector;
		return this;
	}

	public OkHttp3Builder dispatcher(Dispatcher dispatcher) {
		this.dispatcher = dispatcher;
		return this;
	}

}
