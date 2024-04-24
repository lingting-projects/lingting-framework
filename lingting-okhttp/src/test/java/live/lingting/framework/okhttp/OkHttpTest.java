package live.lingting.framework.okhttp;

import live.lingting.framework.http.HttpUrlBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author lingting 2024-01-29 16:39
 */
class OkHttpTest {

	@Test
	void test() {
		OkHttp3 okHttp3 = OkHttp3Builder.builder().build();
		String body = okHttp3.get(HttpUrlBuilder.builder().host("www.baidu.com").build(), String.class);
		assertNotNull(body);
		assertTrue(body.contains("百度"));
	}

}
