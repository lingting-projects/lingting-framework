package live.lingting.framework.http.okhttp;

import live.lingting.framework.http.HttpDelegateClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author lingting 2024-05-07 15:11
 */
class OkHttpDelegateClientTest {

	OkHttpDelegateClient delegate;

	HttpClient client;

	@BeforeEach
	void before() {
		delegate = HttpDelegateClient.okhttp().infiniteTimeout().build();
		client = HttpClient.newBuilder().build();
	}

	@Test
	void request() throws IOException, InterruptedException {
		assertGet();
		assertPost();
	}

	void assertGet() throws IOException, InterruptedException {
		HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create("https://www.baidu.com"));
		HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

		HttpResponse<String> delegated = delegate(builder.build(), handler);
		HttpResponse<String> raw = raw(builder.build(), handler);

		assertNotNull(delegated.body());
		assertNotNull(raw.body());

		assertEquals(raw.body(), delegated.body());
	}

	void assertPost() throws IOException, InterruptedException {
		HttpRequest.Builder builder = HttpRequest.newBuilder()
			.POST(HttpRequest.BodyPublishers.ofString("user_login=sunlisten@foxmail.com"))
			.uri(URI.create("https://gitee.com/check_user_login"));

		HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

		HttpResponse<String> delegated = delegate(builder.build(), handler);
		HttpResponse<String> raw = raw(builder.build(), handler);

		assertEquals(raw.body(), delegated.body());
	}

	<T> HttpResponse<T> delegate(HttpRequest request, HttpResponse.BodyHandler<T> handler) throws IOException {
		return delegate.request(request, handler);
	}

	<T> HttpResponse<T> raw(HttpRequest request, HttpResponse.BodyHandler<T> handler)
			throws IOException, InterruptedException {
		return client.send(request, handler);
	}

}
