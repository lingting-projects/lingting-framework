package live.lingting.framework.http.okhttp;

import lombok.RequiredArgsConstructor;
import okhttp3.Headers;
import okhttp3.Protocol;
import okhttp3.Response;

import javax.net.ssl.SSLSession;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

/**
 * @author lingting 2024-05-07 14:41
 */
@RequiredArgsConstructor
public class OkHttpResponse<T> implements HttpResponse<T> {

	private final HttpRequest request;

	private final HttpResponse.ResponseInfo info;

	private final T t;

	@Override
	public int statusCode() {
		return info.statusCode();
	}

	@Override
	public HttpRequest request() {
		return request;
	}

	@Override
	public Optional<HttpResponse<T>> previousResponse() {
		return Optional.empty();
	}

	@Override
	public HttpHeaders headers() {
		return info.headers();
	}

	@Override
	public T body() {
		return t;
	}

	@Override
	public Optional<SSLSession> sslSession() {
		return Optional.empty();
	}

	@Override
	public URI uri() {
		return request.uri();
	}

	@Override
	public HttpClient.Version version() {
		return info.version();
	}

	public static class ResponseInfo implements HttpResponse.ResponseInfo {

		private final int code;

		private final HttpHeaders headers;

		private final HttpClient.Version version;

		public ResponseInfo(Response response) {
			Headers pairs = response.headers();
			Protocol protocol = response.protocol();

			this.code = response.code();
			this.headers = HttpHeaders.of(pairs.toMultimap(), (x, y) -> true);
			this.version = protocol == Protocol.HTTP_2 ? HttpClient.Version.HTTP_2 : HttpClient.Version.HTTP_1_1;
		}

		@Override
		public int statusCode() {
			return code;
		}

		@Override
		public HttpHeaders headers() {
			return headers;
		}

		@Override
		public HttpClient.Version version() {
			return version;
		}

	}

}
