package live.lingting.framework.okhttp.download;

import live.lingting.framework.download.Download;
import live.lingting.framework.exception.DownloadException;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;

/**
 * @author lingting 2023-12-20 16:43
 */
public interface OkHttpDownload extends Download {

	static OkHttpDownloadBuilder builder(String url) {
		return new OkHttpDownloadBuilder(url);
	}

	static OkHttpDownloadBuilder single(String url) {
		return new OkHttpDownloadBuilder(url).single();
	}

	static OkHttpDownloadBuilder multi(String url) {
		return new OkHttpDownloadBuilder(url).multi();
	}

	@Override
	OkHttpDownload start() throws IOException;

	@Override
	OkHttpDownload await() throws InterruptedException;

	default ResponseBody getBody(Response response) {
		if (!response.isSuccessful()) {
			throw new DownloadException(String.format("response status: %d", response.code()));
		}
		ResponseBody body = response.body();
		if (body == null) {
			throw new DownloadException("download body is null!");
		}
		return body;
	}

}
