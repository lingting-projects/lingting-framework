package live.lingting.framework.http;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author lingting 2024-05-08 14:22
 */
class HttpClientTest {

	java.net.http.HttpClient client;

	@BeforeEach
	void before() {
		client = java.net.http.HttpClient.newBuilder().build();
	}

	@Test
	void test() throws IOException, InterruptedException {
		JavaHttpClient java = HttpClient.java().infiniteTimeout().memoryCookie().build();
		assertClient(java);
		OkHttpClient okhttp = HttpClient.okhttp().infiniteTimeout().memoryCookie().build();
		assertClient(okhttp);
	}

	void assertClient(HttpClient http) throws IOException, InterruptedException {
		assertGet(http);
		assertPost(http);
		assertCookie(http);
	}

	void assertGet(HttpClient http) throws IOException, InterruptedException {
		HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create("https://www.baidu.com"));
		HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

		HttpResponse<String> httpResponse = http.request(builder.build(), handler);
		HttpResponse<String> raw = client.send(builder.build(), handler);

		assertNotNull(httpResponse.body());
		assertNotNull(raw.body());

		assertEquals(raw.body(), httpResponse.body());
	}

	void assertPost(HttpClient http) throws IOException, InterruptedException {
		HttpRequest.Builder builder = HttpRequest.newBuilder()
			.POST(HttpRequest.BodyPublishers.ofString("user_login=sunlisten@foxmail.com"))
			.uri(URI.create("https://gitee.com/check_user_login"));

		HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

		HttpResponse<String> httpResponse = http.request(builder.build(), handler);
		HttpResponse<String> raw = client.send(builder.build(), handler);

		assertEquals(raw.body(), httpResponse.body());
	}

	void assertCookie(HttpClient http) {
		CookieStore cookie = http.cookie();
		assertNotNull(cookie);
		List<HttpCookie> cookies = cookie.getCookies();
		assertFalse(cookies.isEmpty());
	}

}
