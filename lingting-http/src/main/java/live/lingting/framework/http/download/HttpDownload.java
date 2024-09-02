package live.lingting.framework.http.download;

import live.lingting.framework.download.Download;
import live.lingting.framework.exception.DownloadException;
import live.lingting.framework.http.HttpClient;
import live.lingting.framework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * @author lingting 2023-12-20 16:43
 */
@SuppressWarnings("java:S1452")
public interface HttpDownload extends Download {

	int DEFAULT_SIZE = 10 * 1024 * 1024;

	static HttpDownloadBuilder builder(String url) {
		return new HttpDownloadBuilder(url);
	}

	static HttpDownloadBuilder single(String url) {
		return new HttpDownloadBuilder(url).single();
	}

	static HttpDownloadBuilder multi(String url) {
		return new HttpDownloadBuilder(url).multi();
	}

	static HttpDownloadBuilder builder(URI url) {
		return new HttpDownloadBuilder(url);
	}

	static HttpDownloadBuilder single(URI url) {
		return new HttpDownloadBuilder(url).single();
	}

	static HttpDownloadBuilder multi(URI url) {
		return new HttpDownloadBuilder(url).multi();
	}

	HttpClient client();

	@Override
	HttpDownload start() throws IOException;

	@Override
	HttpDownload await() throws InterruptedException;

	default void write(HttpRequest request, OutputStream output) throws IOException {
		HttpClient client = client();
		HttpResponse<InputStream> response = client.request(request, HttpResponse.BodyHandlers.ofInputStream());

		int code = response.statusCode();
		if (code < 200 || code > 299) {
			throw new DownloadException(String.format("response status: %d", code));
		}

		try (InputStream input = response.body()) {
			StreamUtils.write(input, output, DEFAULT_SIZE);
		}

	}

}
