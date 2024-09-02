package live.lingting.framework.http.download;

import live.lingting.framework.download.AbstractMultiDownload;
import live.lingting.framework.http.HttpClient;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * @author lingting 2024-01-15 17:35
 */
@Slf4j
@Getter
public class HttpMultiDownload extends AbstractMultiDownload<HttpMultiDownload> implements HttpDownload {

	protected final HttpClient client;

	protected HttpMultiDownload(HttpDownloadBuilder builder) {
		super(builder);
		this.client = builder.client;
	}

	@Override
	protected long remoteSize() throws IOException {
		HttpRequest.Builder builder = HttpRequest.newBuilder().uri(url).header("Accept-Encoding", "identity");
		HttpResponse<InputStream> response = client.request(builder.build(), HttpResponse.BodyHandlers.ofInputStream());
		HttpHeaders headers = response.headers();
		return headers.firstValueAsLong("Content-Length").orElse(0);
	}

	@Override
	protected void write(OutputStream output, long start, long end) throws IOException {
		HttpRequest.Builder builder = HttpRequest.newBuilder()
			.GET()
			.uri(url)
			.header("Range", String.format("bytes=%d-%d", start, end));
		write(builder.build(), output);
	}

	@Override
	public HttpClient client() {
		return client;
	}

}
