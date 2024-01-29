package live.lingting.framework.okhttp.download;

import live.lingting.framework.download.AbstractSingleDownload;
import live.lingting.framework.okhttp.OkHttp;
import live.lingting.framework.util.StreamUtils;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author lingting 2023-12-20 17:01
 */
public class OkHttpSingleDownload extends AbstractSingleDownload<OkHttpSingleDownload> implements OkHttpDownload {

	protected final OkHttp client;

	protected OkHttpSingleDownload(OkHttpDownloadBuilder builder) {
		super(builder);
		this.client = builder.client;
	}

	@Override
	protected void write(OutputStream out) throws IOException {
		try (Response response = client.get(url)) {
			ResponseBody body = getBody(response);

			InputStream stream = body.byteStream();
			// 写入流到文件
			StreamUtils.write(stream, out, 10 * 1024 * 1024);
		}
	}

}
