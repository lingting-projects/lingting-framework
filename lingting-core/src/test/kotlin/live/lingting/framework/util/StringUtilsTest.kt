package live.lingting.framework.util

import java.nio.charset.StandardCharsets
import live.lingting.framework.util.StringUtils.base64
import live.lingting.framework.util.StringUtils.hex
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * @author lingting 2023-06-25 16:56
 */
internal class StringUtilsTest {
    @Test
    fun underscoreToHump() {
        val raw = "contact_id"
        val hump = StringUtils.underscoreToHump(raw)
        assertEquals("contactId", hump)
    }

    @Test
    fun hex() {
        val hex = "9ab8bc5c3e1792047b699f5c487d25f8"
        val bytes = byteArrayOf(-102, -72, -68, 92, 62, 23, -110, 4, 123, 105, -97, 92, 72, 125, 37, -8)
        assertArrayEquals(bytes, hex.hex())
        assertEquals(hex, bytes.hex())
    }

    @Test
    fun base64() {
        val source = "Base64原文"
        val bytes = source.toByteArray(StandardCharsets.UTF_8)
        val base64 = "QmFzZTY05Y6f5paH"

        assertEquals(base64, bytes.base64())
        assertEquals(source, String(base64.base64(), StandardCharsets.UTF_8))
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
