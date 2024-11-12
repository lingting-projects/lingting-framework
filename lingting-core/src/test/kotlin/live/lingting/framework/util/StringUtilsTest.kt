package live.lingting.framework.util

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.nio.charset.StandardCharsets

/**
 * @author lingting 2023-06-25 16:56
 */
internal class StringUtilsTest {
    @Test
    fun underscoreToHump() {
        val raw = "contact_id"
        val hump = StringUtils.underscoreToHump(raw)
        Assertions.assertEquals("contactId", hump)
    }

    @Test
    fun hex() {
        val hex = "9ab8bc5c3e1792047b699f5c487d25f8"
        val bytes = byteArrayOf(-102, -72, -68, 92, 62, 23, -110, 4, 123, 105, -97, 92, 72, 125, 37, -8)
        assertArrayEquals(bytes, StringUtils.hex(hex))
        assertEquals(hex, StringUtils.hex(bytes))
    }

    @Test
    fun base64() {
        val source = "Base64原文"
        val bytes = source.toByteArray(StandardCharsets.UTF_8)
        val base64 = "QmFzZTY05Y6f5paH"

        assertEquals(base64, StringUtils.base64(bytes))
        Assertions.assertEquals(source, String(StringUtils.base64(base64), StandardCharsets.UTF_8))
    }

    @Test
    fun substring() {
        val source = "www.baidu.com/api/post"
        assertEquals("www.baidu.com", StringUtils.substringBefore(source, "/"))
        assertEquals("www.baidu.com/api", StringUtils.substringBeforeLast(source, "/"))
        assertEquals("api/post", StringUtils.substringAfter(source, "/"))
        assertEquals("post", StringUtils.substringAfterLast(source, "/"))
    }
}
