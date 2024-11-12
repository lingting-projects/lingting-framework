package live.lingting.framework.http.download;

import live.lingting.framework.download.DownloadBuilder;
import live.lingting.framework.http.HttpClient;

import java.net.URI;
import java.time.Duration;

/**
 * @author lingting 2023-12-20 16:49
 */
public class HttpDownloadBuilder extends DownloadBuilder<HttpDownloadBuilder> {

	static final HttpClient DEFAULT_CLIENT = HttpClient.okhttp()
		.disableSsl()
		.callTimeout(Duration.ofSeconds(10))
		.connectTimeout(Duration.ofSeconds(10))
		.readTimeout(Duration.ofSeconds(10))
		.build();

	/**
	 * 客户端配置
	 */
	protected HttpClient client = DEFAULT_CLIENT;

	protected HttpDownloadBuilder(String url) {
		super(url);
	}

	protected HttpDownloadBuilder(URI url) {
		this(url.toString());
	}

	public HttpDownloadBuilder client(HttpClient client) {
		this.client = client;
		return this;
	}


	public HttpDownload build() {
		return new HttpDownload(this);
	}

}
