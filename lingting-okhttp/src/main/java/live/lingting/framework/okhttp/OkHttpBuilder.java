package live.lingting.framework.okhttp;

import live.lingting.framework.http.HostnameAllVerifier;
import live.lingting.framework.http.X509TrustAllManager;
import lombok.Getter;
import okhttp3.CookieJar;

import javax.net.SocketFactory;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import java.time.Duration;
import java.util.function.UnaryOperator;

/**
 * @author lingting 2023/2/2 17:26
 */
@Getter
@SuppressWarnings("java:S1700")
public class OkHttpBuilder {

	private okhttp3.OkHttpClient.Builder okHttpClientBuilder = new okhttp3.OkHttpClient.Builder();

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

	private CookieJar cookieJar = null;

	public static OkHttpBuilder builder() {
		return new OkHttpBuilder();
	}

	public okhttp3.OkHttpClient.Builder builder(okhttp3.OkHttpClient.Builder builder) {
		if (builder == null) {
			builder = new okhttp3.OkHttpClient.Builder();
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

		return builder;
	}

	public OkHttp build() {
		return build(okHttpClientBuilder);
	}

	public OkHttp build(okhttp3.OkHttpClient.Builder clientBuilder) {
		return new OkHttp(builder(clientBuilder).build());
	}

	public OkHttp build(UnaryOperator<okhttp3.OkHttpClient.Builder> operator) {
		okhttp3.OkHttpClient.Builder build = builder(okHttpClientBuilder);
		return new OkHttp(operator.apply(build).build());
	}

	public OkHttpBuilder okHttpClientBuilder(okhttp3.OkHttpClient.Builder httpClientBuilder) {
		this.okHttpClientBuilder = httpClientBuilder;
		return this;
	}

	public OkHttpBuilder socketFactory(SocketFactory socketFactory) {
		this.socketFactory = socketFactory;
		return this;
	}

	public OkHttpBuilder hostnameVerifier(HostnameVerifier hostnameVerifier) {
		this.hostnameVerifier = hostnameVerifier;
		return this;
	}

	public OkHttpBuilder sslSocketFactory(SSLSocketFactory ssf) {
		this.sslSocketFactory = ssf;
		return this;
	}

	public OkHttpBuilder trustManager(X509TrustManager trustManager) {
		this.trustManager = trustManager;
		return this;
	}

	public OkHttpBuilder disableSsl() {
		return sslSocketFactory((SSLSocketFactory) SSLSocketFactory.getDefault())
			.trustManager(new X509TrustAllManager())
			.hostnameVerifier(new HostnameAllVerifier());
	}

	public OkHttpBuilder callTimeout(Duration callTimeout) {
		this.callTimeout = callTimeout;
		return this;
	}

	public OkHttpBuilder connectTimeout(Duration connectTimeout) {
		this.connectTimeout = connectTimeout;
		return this;
	}

	public OkHttpBuilder readTimeout(Duration readTimeout) {
		this.readTimeout = readTimeout;
		return this;
	}

	public OkHttpBuilder writeTimeout(Duration writeTimeout) {
		this.writeTimeout = writeTimeout;
		return this;
	}

	/**
	 * 无限等待时间
	 */
	public OkHttpBuilder infiniteTimeout() {
		return timeout(Duration.ZERO, Duration.ZERO, Duration.ZERO, Duration.ZERO);
	}

	public OkHttpBuilder timeout(Duration connectTimeout, Duration readTimeout) {
		return connectTimeout(connectTimeout).readTimeout(readTimeout);
	}

	public OkHttpBuilder timeout(Duration callTimeout, Duration connectTimeout, Duration readTimeout,
			Duration writeTimeout) {
		return callTimeout(callTimeout).connectTimeout(connectTimeout)
			.readTimeout(readTimeout)
			.writeTimeout(writeTimeout);
	}

	public OkHttpBuilder cookieJar(CookieJar jar) {
		this.cookieJar = jar;
		return this;
	}

	public OkHttpBuilder keepCookieJar() {
		return cookieJar(new OkHttpKeepCookieJar());
	}

}
