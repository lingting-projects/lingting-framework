package live.lingting.framework.http

import live.lingting.framework.util.ProxySelectorUtils
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.net.InetSocketAddress
import java.net.ProxySelector
import java.net.URI

/**
 * @author lingting 2024-05-08 14:22
 */
class HttpClientTest {

    var client: HttpClient? = null

    var selector: ProxySelector? = null

    var useProxy: Boolean = false

    @BeforeEach
    fun before() {
        client = OkHttpClient.Builder().build()
        selector = if (!useProxy) null else ProxySelectorUtils.create(InetSocketAddress("127.0.0.1", 8888))
    }

    @Test

    fun test() {
        val okhttp = HttpClient.okhttp()
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
        val builder = HttpRequest.builder()

        // 补充一个参数带 + 号的测试用例

        builder.url(URI.create("https://www.baidu.com"))
        val httpResponse = http.request(builder.build())
        assertNotNull(httpResponse.body())
        val string = assertDoesNotThrow<String?> { httpResponse.string() }
        assertTrue(string!!.contains("<") && string.contains(">"))
        builder.url("https://maven.aliyun.com/repository/central/live/lingting/components/component-validation/0.0.1/component-validation-0.0.1.pom")
        val r2 = http.request(builder.build())
        assertNotNull(r2.body())
        val string2 = assertDoesNotThrow<String?> { r2.string() }
        assertTrue(string2!!.contains("component-validation"))
    }

    fun assertPost(http: HttpClient) {
        val builder = HttpRequest.builder()
            .post()
            .body("user_login=sunlisten@foxmail.com")
            .url(URI.create("https://gitee.com/check_user_login"))

        val httpResponse = http.request(builder.build())
        assertNotNull(httpResponse.body())
        val string = assertDoesNotThrow<String?> { httpResponse.string() }
        assertNotNull(string)
    }

    fun assertCookie(http: HttpClient) {
        val cookie = http.cookie()
        assertNotNull(cookie)
        val cookies = cookie!!.cookies
        assertFalse(cookies.isEmpty())
    }
}
