package live.lingting.framework.http;

import com.fasterxml.jackson.core.type.TypeReference;
import live.lingting.framework.http.header.HttpHeaders;
import live.lingting.framework.jackson.JacksonUtils;
import live.lingting.framework.lock.JavaReentrantLock;
import live.lingting.framework.lock.LockRunnable;
import live.lingting.framework.stream.CloneInputStream;
import live.lingting.framework.util.StreamUtils;
import lombok.SneakyThrows;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.function.Function;

/**
 * @author lingting 2024-09-12 23:37
 */
public class HttpResponse implements Closeable {

	protected final JavaReentrantLock lock = new JavaReentrantLock();

	protected final HttpRequest request;

	protected final int code;

	protected final HttpHeaders headers;

	protected final InputStream body;

	protected String string;

	protected HttpResponse(HttpRequest request, int code, HttpHeaders headers, InputStream body) {
		this.request = request;
		this.code = code;
		this.headers = headers;
		this.body = body;
	}

	public HttpRequest request() {
		return request;
	}

	public URI uri() {
		return request.uri();
	}

	public int code() {
		return code;
	}

	public HttpHeaders headers() {
		return headers;
	}

	public InputStream body() {
		return body;
	}

	public byte[] bytes() throws IOException {
		try (InputStream in = body()) {
			return in.readAllBytes();
		}
	}

	@SneakyThrows
	public String string() {
		if (string != null) {
			return string;
		}

		lock.runByInterruptibly(new LockRunnable() {
			@Override
			@SneakyThrows
			public void run() throws InterruptedException {
				if (string != null) {
					return;
				}
				try (InputStream stream = body()) {
					string = stream == null ? "" : StreamUtils.toString(stream);
				}
			}
		});
		return string;
	}

	public <T> T convert(Class<T> cls) {
		String json = string();
		return JacksonUtils.toObj(json, cls);
	}

	public <T> T convert(Type type) {
		String json = string();
		return JacksonUtils.toObj(json, type);
	}

	public <T> T convert(TypeReference<T> reference) {
		String json = string();
		return JacksonUtils.toObj(json, reference);
	}

	public <T> T convert(Function<String, T> function) {
		String json = string();
		return function.apply(json);
	}

	public boolean isRange(int start, int end) {
		int status = code();
		return status >= start && status <= end;
	}

	public boolean is2xx() {
		return isRange(200, 299);
	}

	@Override
	public void close() throws IOException {
		if (body instanceof CloneInputStream clone) {
			clone.setCloseAndDelete(true);
		}
		StreamUtils.close(body);
	}

}
