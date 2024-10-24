package live.lingting.framework.http;

import live.lingting.framework.http.body.BodySource;
import live.lingting.framework.http.body.MemoryBody;
import live.lingting.framework.http.header.HttpHeaders;
import live.lingting.framework.jackson.JacksonUtils;
import live.lingting.framework.util.StringUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author lingting 2024-09-27 21:29
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpRequest {

	protected final HttpMethod method;

	protected final URI uri;

	protected final HttpHeaders headers;

	protected final Body body;

	public static Builder builder() {
		return new Builder();
	}

	public HttpMethod method() {
		return method;
	}

	public URI uri() {
		return uri;
	}

	public HttpHeaders headers() {
		return headers;
	}

	public Body body() {
		return body;
	}

	@Getter
	public static class Builder {

		private HttpMethod method = HttpMethod.GET;

		private HttpUrlBuilder urlBuilder;

		private final HttpHeaders headers = HttpHeaders.empty();

		private Body body;

		// region method
		public Builder method(HttpMethod method) {
			this.method = method;
			return this;
		}

		public Builder method(String method) {
			return method(HttpMethod.valueOf(method));
		}

		public Builder get() {
			return method(HttpMethod.GET);
		}

		public Builder put() {
			return method(HttpMethod.PUT);
		}

		public Builder post() {
			return method(HttpMethod.POST);
		}

		public Builder delete() {
			return method(HttpMethod.DELETE);
		}

		public Builder head() {
			return method(HttpMethod.HEAD);
		}

		public Builder options() {
			return method(HttpMethod.OPTIONS);
		}

		public Builder patch() {
			return method(HttpMethod.PATCH);
		}

		// endregion

		// region url
		public Builder url(String url) {
			return url(URI.create(url));
		}

		public Builder url(URI url) {
			this.urlBuilder = HttpUrlBuilder.from(url);
			return this;
		}

		public Builder url(Consumer<HttpUrlBuilder> consumer) {
			consumer.accept(urlBuilder);
			return this;
		}

		public Builder url(HttpUrlBuilder urlBuilder) {
			this.urlBuilder = urlBuilder;
			return this;
		}

		// endregion

		// region headers

		public Builder header(String name, String value) {
			this.headers.add(name, value);
			return this;
		}

		public Builder headers(String name, Collection<String> values) {
			this.headers.addAll(name, values);
			return this;
		}

		public Builder headers(Map<String, Collection<String>> map) {
			this.headers.addAll(map);
			return this;
		}

		public Builder headers(HttpHeaders headers) {
			this.headers.addAll(headers);
			return this;
		}

		public Builder headers(Consumer<HttpHeaders> consumer) {
			consumer.accept(headers);
			return this;
		}

		// endregion

		// region body

		public Builder body(Body body) {
			this.body = body;
			String contentType = body.contentType();
			headers.contentType(contentType);
			return this;
		}

		public Builder body(BodySource body) {
			return body(Body.of(body, headers.contentType()));
		}

		public Builder body(String body) {
			return body(new MemoryBody(body));
		}

		public Builder jsonBody(Object obj) {
			return body(JacksonUtils.toJson(obj));
		}

		// endregion

		public HttpRequest build() {
			Body requestBody = body == null ? Body.empty() : body;
			String contentType = headers.contentType();
			if (!StringUtils.hasText(contentType)) {
				String type = requestBody.contentType();
				if (StringUtils.hasText(type)) {
					headers.contentType(type);
				}
			}
			URI uri = urlBuilder.buildUri();
			headers.host(uri.getHost());
			return new HttpRequest(method, uri, headers.unmodifiable(), requestBody);
		}

	}

	/**
	 * @author lingting 2024-09-28 11:54
	 */
	@RequiredArgsConstructor
	public static class Body {

		private final BodySource source;

		private final String contentType;

		public static Body empty() {
			return new Body(BodySource.empty(), null);
		}

		public static Body of(BodySource body) {
			return new Body(body, null);
		}

		public static Body of(BodySource body, String contentType) {
			return new Body(body, contentType);
		}

		public String contentType() {
			return contentType;
		}

		public long contentLength() {
			return source.length();
		}

		public BodySource source() {
			return source;
		}

		public byte[] bytes() {
			return source.bytes();
		}

		public InputStream input() {
			return source.openInput();
		}

		public String string() {
			return string(StandardCharsets.UTF_8);
		}

		@SneakyThrows
		public String string(Charset charset) {
			return source.string(charset);
		}

	}

}
