package live.lingting.framework.http

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

/**
 * @author lingting 2024-01-29 16:31
 */
internal class HttpUrlBuilderTest {
    @Test
    fun test() {
        val builder = HttpUrlBuilder.builder().https().host("www.baidu.com")
        Assertions.assertEquals("https://www.baidu.com", builder.build())
        builder.uri("search").http()
        Assertions.assertEquals("http://www.baidu.com/search", builder.build())
        Assertions.assertEquals("http://www.baidu.com/search", builder.buildUri().toString())
        builder.https().addParam("q1", "q1").addParam("q2", "q2")
        Assertions.assertEquals("https://www.baidu.com/search?q1=q1&q2=q2", builder.build())
        builder.http().host("https://www.google.com")
        Assertions.assertEquals("https://www.google.com/search?q1=q1&q2=q2", builder.build())
        builder.port(80).http()
        Assertions.assertEquals("http://www.google.com:80/search?q1=q1&q2=q2", builder.build())
        val copy = builder.copy().https()
        Assertions.assertEquals("https://www.google.com:80/search?q1=q1&q2=q2", copy.build())

        copy.addParam("q3", listOf("q31", "q32"))
        Assertions.assertEquals("https://www.google.com:80/search?q1=q1&q2=q2&q3=q31&q3=q32", copy.build())
        copy.uriSegment("a").uriSegment("b", "c")
        Assertions.assertEquals("https://www.google.com:80/search/a/b/c?q1=q1&q2=q2&q3=q31&q3=q32", copy.build())
        copy.addParam("q4", "s p a c e")
        Assertions.assertEquals("https://www.google.com:80/search/a/b/c?q1=q1&q2=q2&q3=q31&q3=q32&q4=s p a c e", copy.build())
        val uri = copy.buildUri()
        Assertions.assertEquals(
            "https://www.google.com:80/search/a/b/c?q1=q1&q2=q2&q3=q31&q3=q32&q4=s%20p%20a%20c%20e",
            uri.toString()
        )
        val from = HttpUrlBuilder.from(uri)
        Assertions.assertEquals("https://www.google.com:80/search/a/b/c?q1=q1&q2=q2&q3=q31&q3=q32&q4=s p a c e", from.build())
    }
}
