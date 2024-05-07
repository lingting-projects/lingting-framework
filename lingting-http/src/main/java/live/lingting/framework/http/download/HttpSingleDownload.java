package live.lingting.framework.http.download;

import live.lingting.framework.download.AbstractSingleDownload;
import live.lingting.framework.http.HttpDelegateClient;

import java.io.IOException;
import java.io.OutputStream;
import java.net.http.HttpRequest;

/**
 * @author lingting 2023-12-20 17:01
 */
public class HttpSingleDownload extends AbstractSingleDownload<HttpSingleDownload> implements HttpDownload {

	protected final HttpDelegateClient<?> client;

	protected HttpSingleDownload(HttpDownloadBuilder builder) {
		super(builder);
		this.client = builder.client;
	}

	@Override
	protected void write(OutputStream out) throws IOException {
		HttpRequest request = HttpRequest.newBuilder().GET().uri(url).build();
		write(request, out);
	}

	@Override
	public HttpDelegateClient<?> client() {
		return client;
	}

}
