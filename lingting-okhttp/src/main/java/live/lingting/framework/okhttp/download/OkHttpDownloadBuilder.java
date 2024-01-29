package live.lingting.framework.okhttp.download;

import live.lingting.framework.download.AbstractDownloadBuilder;
import live.lingting.framework.okhttp.OkHttp;

import java.time.Duration;

/**
 * @author lingting 2023-12-20 16:49
 */
public class OkHttpDownloadBuilder extends AbstractDownloadBuilder<OkHttpDownloadBuilder> {

	static final OkHttp DEFAULT_CLIENT = OkHttp.builder()
		.disableSsl()
		.callTimeout(Duration.ofSeconds(10))
		.connectTimeout(Duration.ofSeconds(10))
		.readTimeout(Duration.ofSeconds(10))
		.build();

	/**
	 * 客户端配置
	 */
	OkHttp client = DEFAULT_CLIENT;

	protected OkHttpDownloadBuilder(String url) {
		super(url);
	}

	public OkHttpDownloadBuilder client(OkHttp client) {
		this.client = client;
		return this;
	}

	public OkHttpDownload build() {
		return multi ? new OkHttpMultiDownload(this) : new OkHttpSingleDownload(this);
	}

}
