package live.lingting.framework.http;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author lingting 2024-01-29 16:31
 */
class HttpUrlBuilderTest {

	@Test
	void test() {
		HttpUrlBuilder builder = HttpUrlBuilder.builder().https().host("www.baidu.com");
		assertEquals("https://www.baidu.com/?", builder.build());
		builder.uri("search").http();
		assertEquals("http://www.baidu.com/search?", builder.build());
		builder.https().addParam("q1", "q1").addParam("q2", "q2");
		assertEquals("https://www.baidu.com/search?&q1=q1&q2=q2", builder.build());
		builder.http().host("https://www.google.com");
		assertEquals("https://www.google.com/search?&q1=q1&q2=q2", builder.build());
		builder.port(80).http();
		assertEquals("https://www.google.com:80/search?&q1=q1&q2=q2", builder.build());
	}

}
