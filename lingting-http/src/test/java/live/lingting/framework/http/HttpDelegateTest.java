package live.lingting.framework.http;

import live.lingting.framework.http.java.JavaHttpDelegateClient;
import live.lingting.framework.http.okhttp.OkHttpDelegateClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author lingting 2024-05-08 14:22
 */
class HttpDelegateTest {

	HttpClient client;

	@BeforeEach
	void before() {
		client = HttpClient.newBuilder().build();
	}

	@Test
	void test() throws IOException, InterruptedException {
		JavaHttpDelegateClient java = HttpDelegateClient.java().infiniteTimeout().memoryCookie().build();
		assertClient(java);
		OkHttpDelegateClient okhttp = HttpDelegateClient.okhttp().infiniteTimeout().memoryCookie().build();
		assertClient(okhttp);
	}

	void assertClient(HttpDelegateClient<?> delegate) throws IOException, InterruptedException {
		assertGet(delegate);
		assertPost(delegate);
		assertCookie(delegate);
	}

	void assertGet(HttpDelegateClient<?> delegate) throws IOException, InterruptedException {
		HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create("https://www.baidu.com"));
		HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

		HttpResponse<String> delegated = delegate.request(builder.build(), handler);
		HttpResponse<String> raw = client.send(builder.build(), handler);

		assertNotNull(delegated.body());
		assertNotNull(raw.body());

		assertEquals(raw.body(), delegated.body());
	}

	void assertPost(HttpDelegateClient<?> delegate) throws IOException, InterruptedException {
		HttpRequest.Builder builder = HttpRequest.newBuilder()
			.POST(HttpRequest.BodyPublishers.ofString("user_login=sunlisten@foxmail.com"))
			.uri(URI.create("https://gitee.com/check_user_login"));

		HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

		HttpResponse<String> delegated = delegate.request(builder.build(), handler);
		HttpResponse<String> raw = client.send(builder.build(), handler);

		assertEquals(raw.body(), delegated.body());
	}

	void assertCookie(HttpDelegateClient<?> delegate) throws IOException, InterruptedException {
		CookieStore cookie = delegate.cookie();
		assertNotNull(cookie);
		List<HttpCookie> cookies = cookie.getCookies();
		assertFalse(cookies.isEmpty());
	}

}
