package live.lingting.framework.http.download;

import live.lingting.framework.download.MultipartDownload;
import live.lingting.framework.exception.DownloadException;
import live.lingting.framework.http.HttpClient;
import live.lingting.framework.multipart.Part;
import live.lingting.framework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * @author lingting 2023-12-20 16:43
 */
@SuppressWarnings("java:S1452")
public class HttpDownload extends MultipartDownload<HttpDownload> {

	protected final HttpClient client;

	protected final URI uri;

	public HttpDownload(HttpDownloadBuilder builder) throws IOException {
		super(builder);
		this.client = builder.client;
		this.uri = URI.create(url);
	}

	public static HttpDownloadBuilder builder(String url) {
		return new HttpDownloadBuilder(url);
	}

	public static HttpDownloadBuilder single(String url) {
		return new HttpDownloadBuilder(url).single();
	}

	public static HttpDownloadBuilder multi(String url) {
		return new HttpDownloadBuilder(url).multi();
	}

	public static HttpDownloadBuilder builder(URI url) {
		return new HttpDownloadBuilder(url);
	}

	public static HttpDownloadBuilder single(URI url) {
		return new HttpDownloadBuilder(url).single();
	}

	public static HttpDownloadBuilder multi(URI url) {
		return new HttpDownloadBuilder(url).multi();
	}

	void write(HttpRequest request, OutputStream output) throws IOException {
		HttpResponse<InputStream> response = client.request(request, HttpResponse.BodyHandlers.ofInputStream());

		int code = response.statusCode();
		if (code < 200 || code > 299) {
			throw new DownloadException(String.format("response status: %d", code));
		}

		try (InputStream input = response.body()) {
			StreamUtils.write(input, output);
		}

	}

	@Override
	public long size() throws IOException {
		HttpRequest.Builder builder = HttpRequest.newBuilder().uri(uri).header("Accept-Encoding", "identity");
		HttpResponse<InputStream> response = client.request(builder.build(), HttpResponse.BodyHandlers.ofInputStream());
		HttpHeaders headers = response.headers();
		return headers.firstValueAsLong("Content-Length").orElse(0);
	}

	@Override
	public InputStream download(Part part) throws Exception {
		HttpRequest.Builder builder = HttpRequest.newBuilder().GET().uri(uri);
		if (multi) {
			builder.header("Range", String.format("bytes=%d-%d", part.getStart(), part.getEnd()));
		}

		HttpRequest request = builder.build();
		HttpResponse<InputStream> response = client.request(request, HttpResponse.BodyHandlers.ofInputStream());

		int code = response.statusCode();
		if (code < 200 || code > 299) {
			throw new DownloadException(String.format("response status: %d", code));
		}

		return response.body();
	}

}
