package live.lingting.framework.http

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * @author lingting 2024-01-29 16:31
 */
class HttpUrlBuilderTest {

    @Test
    fun testDomain() {
        val builder = HttpUrlBuilder.builder().https().host("www.baidu.com")
        assertEquals("https://www.baidu.com/", builder.build())
        builder.path("search").http()
        assertEquals("http://www.baidu.com/search", builder.build())
        assertEquals("http://www.baidu.com/search", builder.buildUri().toString())
        builder.https().addParam("q1", "q1").addParam("q2", "q2")
        assertEquals("https://www.baidu.com/search?q1=q1&q2=q2", builder.build())
        builder.http().host("https://www.google.com")
        assertEquals("https://www.google.com/search?q1=q1&q2=q2", builder.build())
        builder.port(80).http()
        assertEquals("http://www.google.com/search?q1=q1&q2=q2", builder.build())
        val copy = builder.copy().https()
        assertEquals("https://www.google.com:80/search?q1=q1&q2=q2", copy.build())

        copy.addParam("q3", listOf("q31", "q32"))
        assertEquals("https://www.google.com:80/search?q1=q1&q2=q2&q3=q31&q3=q32", copy.build())
        copy.pathSegment("a").pathSegment("b", "c")
        assertEquals("https://www.google.com:80/search/a/b/c?q1=q1&q2=q2&q3=q31&q3=q32", copy.build())
        copy.addParam("q4", "s p a c e")
        assertEquals("https://www.google.com:80/search/a/b/c?q1=q1&q2=q2&q3=q31&q3=q32&q4=s+p+a+c+e", copy.build())
        val uri = copy.buildUri()
        assertEquals(
            "https://www.google.com:80/search/a/b/c?q1=q1&q2=q2&q3=q31&q3=q32&q4=s+p+a+c+e",
            uri.toString()
        )
        val from = HttpUrlBuilder.from(uri)
        assertEquals(
            "https://www.google.com:80/search/a/b/c?q1=q1&q2=q2&q3=q31&q3=q32&q4=s+p+a+c+e",
            from.build()
        )
        val repeat = HttpUrlBuilder.builder().host("https://www.baidu.com/").pathSegment("/a/").pathSegment("/b/")
        assertEquals("https://www.baidu.com/a/b", repeat.build())
    }

    @Test
    fun testIp() {
        val builder = HttpUrlBuilder.builder().https().host("192.168.1.1")
        assertEquals("https://192.168.1.1/", builder.build())
        assertEquals("https://192.168.1.1/", builder.buildUri().toString())
        assertEquals("192.168.1.1", builder.headerHost())
        builder.host("192.168.1.1:90")
        assertEquals(90, builder.port())
        assertEquals("https://192.168.1.1:90/", builder.build())
        assertEquals("https://192.168.1.1:90/", builder.buildUri().toString())
        assertEquals("https://192.168.1.1:90/", builder.buildUrl().toString())
        assertEquals("192.168.1.1:90", builder.headerHost())
    }

    @Test
    fun testSpecial() {
        assertEquals("i=a+b", HttpUrlBuilder.from("http://127.0.0.1:202/a?i=a b").buildQuery())
        assertEquals("i=a+b", HttpUrlBuilder.from("http://127.0.0.1:202/a?i=a+b").buildQuery())
        assertEquals("i=a+b", HttpUrlBuilder.from("http://127.0.0.1:202/a?i=a%20b").buildQuery())
        var from = HttpUrlBuilder.from("http://127.0.0.1:202/#hash/a?i=a b")
        assertEquals("i=a+b", from.buildQuery())
        assertEquals("/#hash/a", from.buildPath())
        from = HttpUrlBuilder.from("http://127.0.0.1:202#/hash/a?i=a b")
        assertEquals("i=a+b", from.buildQuery())
        assertEquals("/#/hash/a", from.buildPath())
        from = HttpUrlBuilder.from("http://127.0.0.1:202#hash/a?i=a b")
        assertEquals("i=a+b", from.buildQuery())
        assertEquals("/#hash/a", from.buildPath())
        from = HttpUrlBuilder.from("http://127.0.0.1:202#hash/a")
        assertEquals("/#hash/a", from.buildPath())
        from = HttpUrlBuilder.from("http://127.0.0.1:202?i=a b")
        assertEquals("i=a+b", from.buildQuery())
    }

}
