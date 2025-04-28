package live.lingting.framework.util

import live.lingting.framework.util.DataSizeUtils.bytes
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream

/**
 * @author lingting 2024-09-05 19:48
 */
class DigestUtilsTest {
    @Test

    fun md5Hex() {
        val source = "hello md5, digest to hex."
        val bytes = source.toByteArray()
        val hex = "6ac2b48dd5c8584e0882aea82196f5df"

        assertEquals(hex, DigestUtils.md5Hex(source))
        assertEquals(hex, DigestUtils.md5Hex(bytes))
        assertEquals(hex, DigestUtils.md5Hex(ByteArrayInputStream(bytes)))
        assertEquals(hex, DigestUtils.md5Hex(ByteArrayInputStream(bytes), 1.bytes))
    }

    @Test

    fun sha1Hex() {
        val source = "hello sha1, digest to hex."
        val bytes = source.toByteArray()
        val hex = "1bc582ed92e12f1f16c59118fe6d4f4122db7d61"

        assertEquals(hex, DigestUtils.sha1Hex(source))
        assertEquals(hex, DigestUtils.sha1Hex(bytes))
        assertEquals(hex, DigestUtils.sha1Hex(ByteArrayInputStream(bytes)))
        assertEquals(hex, DigestUtils.sha1Hex(ByteArrayInputStream(bytes), 1.bytes))
    }

    @Test

    fun sha256Hex() {
        val source = "hello sha256, digest to hex."
        val bytes = source.toByteArray()
        val hex = "2723b92c190589606102a284b82f2715b0314a27fc5f9fd5865d66da51a8ac52"

        assertEquals(hex, DigestUtils.sha256Hex(source))
        assertEquals(hex, DigestUtils.sha256Hex(bytes))
        assertEquals(hex, DigestUtils.sha256Hex(ByteArrayInputStream(bytes)))
        assertEquals(hex, DigestUtils.sha256Hex(ByteArrayInputStream(bytes), 1.bytes))
    }
}
