package live.lingting.framework.http

import java.net.InetSocketAddress
import java.net.ProxySelector
import java.net.URI
import live.lingting.framework.http.HttpClient.Companion.java
import live.lingting.framework.http.HttpClient.Companion.okhttp
import live.lingting.framework.http.HttpRequest.Companion.builder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * @author lingting 2024-05-08 14:22
 */
internal class HttpClientTest {
    var client: java.net.http.HttpClient? = null

    var selector: ProxySelector? = null

    var useCharles: Boolean = false

    @BeforeEach
    fun before() {
        client = java.net.http.HttpClient.newBuilder().build()
        selector = if (!useCharles) null else ProxySelector.of(InetSocketAddress("127.0.0.1", 9999))
    }

    @Test

    fun test() {
        val java = java()
            .disableSsl()
            .infiniteTimeout()
            .memoryCookie()
            .proxySelector(selector)
            .build()
        assertClient(java)
        val okhttp = okhttp()
            .disableSsl()
            .infiniteTimeout()
            .memoryCookie()
            .proxySelector(selector)
            .build()
        assertClient(okhttp)
    }


    fun assertClient(http: HttpClient) {
        assertGet(http)
        assertPost(http)
        assertCookie(http)
    }


    fun assertGet(http: HttpClient) {
        val builder = builder().url(URI.create("https://www.baidu.com"))
        val httpResponse = http.request(builder.build())
        Assertions.assertNotNull(httpResponse.body())
        val string = Assertions.assertDoesNotThrow<String?> { httpResponse.string() }
        Assertions.assertTrue(string!!.contains("<") && string.contains(">"))
        builder.url(
            "https://maven.aliyun.com/repository/central/live/lingting/components/component-validation/0.0.1/component-validation-0.0.1.pom"
        )
            .build()
        val r2 = http.request(builder.build())
        Assertions.assertNotNull(r2.body())
        val string2 = Assertions.assertDoesNotThrow<String?> { r2.string() }
        Assertions.assertTrue(string2!!.contains("component-validation"))
    }


    fun assertPost(http: HttpClient) {
        val builder = builder()
            .post()
            .body("user_login=sunlisten@foxmail.com")
            .url(URI.create("https://gitee.com/check_user_login"))

        val httpResponse = http.request(builder.build())
        Assertions.assertNotNull(httpResponse.body())
        val string = Assertions.assertDoesNotThrow<String?> { httpResponse.string() }
        Assertions.assertNotNull(string)
    }

    fun assertCookie(http: HttpClient) {
        val cookie = http.cookie()
        Assertions.assertNotNull(cookie)
        val cookies = cookie!!.cookies
        Assertions.assertFalse(cookies.isEmpty())
    }
}
