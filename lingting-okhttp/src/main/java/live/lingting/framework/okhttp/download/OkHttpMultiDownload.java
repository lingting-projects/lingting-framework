package live.lingting.framework.okhttp.download;

import live.lingting.framework.download.AbstractMultiDownload;
import live.lingting.framework.okhttp.OkHttp;
import live.lingting.framework.util.StreamUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author lingting 2024-01-15 17:35
 */
@Slf4j
@Getter
public class OkHttpMultiDownload extends AbstractMultiDownload<OkHttpMultiDownload> implements OkHttpDownload {

	protected final OkHttp client;

	protected OkHttpMultiDownload(OkHttpDownloadBuilder builder) {
		super(builder);
		this.client = builder.client;
	}

	@Override
	protected long remoteSize() {
		Request.Builder builder = new Request.Builder().url(url)
			// https://github.com/ali-sdk/ali-oss/issues/954#issue-896447718
			.addHeader("Accept-Encoding", "identity");
		try (Response response = client.request(builder.build())) {
			ResponseBody body = getBody(response);
			return body.contentLength();
		}
	}

	@Override
	protected void write(OutputStream output, long start, long end) throws IOException {
		Request.Builder builder = new Request.Builder().url(url)
			.addHeader("Range", String.format("bytes=%d-%d", start, end));

		try (Response response = client.request(builder.build())) {
			ResponseBody body = getBody(response);
			InputStream input = body.byteStream();
			StreamUtils.write(input, output);
		}
	}

}
