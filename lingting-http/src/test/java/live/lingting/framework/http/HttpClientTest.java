package live.lingting.framework.http;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author lingting 2024-05-08 14:22
 */
class HttpClientTest {

	ProxySelector selector;

	boolean useCharles = false;

	@BeforeEach
	void before() {
		selector = !useCharles ? null : new ProxySelector() {
			@Override
			public List<Proxy> select(URI uri) {
				Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 8888));
				List<Proxy> proxies = new java.util.ArrayList<>();
				proxies.add(proxy);
				return proxies;
			}

			@Override
			public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
				//
			}
		};
	}

	@Test
	void test() throws IOException {
		OkHttpClient okhttp = HttpClient.okhttp()
			.disableSsl()
			.infiniteTimeout()
			.memoryCookie()
			.proxySelector(selector)
			.build();
		assertClient(okhttp);
	}

	void assertClient(HttpClient http) throws IOException {
		assertGet(http);
		assertPost(http);
		assertCookie(http);
	}

	void assertGet(HttpClient http) throws IOException {
		HttpRequest.Builder builder = HttpRequest.builder().url(URI.create("https://www.baidu.com"));
		HttpResponse httpResponse = http.request(builder.build());
		assertNotNull(httpResponse.body());
		String string = assertDoesNotThrow(httpResponse::string);
		assertTrue(string.contains("<") && string.contains(">"));
		builder.url(
				"https://maven.aliyun.com/repository/central/live/lingting/components/component-validation/0.0.1/component-validation-0.0.1.pom")
			.build();
		HttpResponse r2 = http.request(builder.build());
		assertNotNull(r2.body());
		String string2 = assertDoesNotThrow(r2::string);
		assertTrue(string2.contains("component-validation"));
	}

	void assertPost(HttpClient http) throws IOException {
		HttpRequest.Builder builder = HttpRequest.builder()
			.post()
			.body("user_login=sunlisten@foxmail.com")
			.url(URI.create("https://gitee.com/check_user_login"));

		HttpResponse httpResponse = http.request(builder.build());
		assertNotNull(httpResponse.body());
		String string = assertDoesNotThrow(httpResponse::string);
		assertNotNull(string);
	}

	void assertCookie(HttpClient http) {
		CookieStore cookie = http.cookie();
		assertNotNull(cookie);
		List<HttpCookie> cookies = cookie.getCookies();
		assertFalse(cookies.isEmpty());
	}

}
