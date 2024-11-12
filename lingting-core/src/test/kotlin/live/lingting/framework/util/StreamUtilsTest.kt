package live.lingting.framework.util

import live.lingting.framework.retry.Retry.value
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets

/**
 * @author lingting 2023-12-22 11:56
 */
internal class StreamUtilsTest {
    val line1: String = "1\r\n2\n3"

    val line2: String = "1\t\r\n"

    val line3: String = "这是一行文本\n这是第二行文本\r\neng"

    fun of(string: String): ByteArrayInputStream {
        return ByteArrayInputStream(string.toByteArray(StandardCharsets.UTF_8))
    }

    @Test
    @Throws(IOException::class)
    fun readLine() {
        StreamUtils.readLine(of(line1), StandardCharsets.UTF_8) { index, line ->
            when (index) {
                0 -> assertEquals("1", line)
                1 -> assertEquals("2", line)
                else -> assertEquals("3", line)
            }
        }
        StreamUtils.readLine(of(line2), StandardCharsets.UTF_8) { index, line ->
            assertEquals(0, index)
            assertEquals("1\t", line)
        }
        StreamUtils.readLine(of(line3), StandardCharsets.UTF_8) { index, line ->
            when (index) {
                0 -> assertEquals("这是一行文本", line)
                1 -> assertEquals("这是第二行文本", line)
                else -> assertEquals("eng", line)
            }
        }
    }

    @Test
    @Throws(IOException::class)
    fun testClone() {
        val clone = StreamUtils.clone(of(line3))
        Assertions.assertEquals(line3, StreamUtils.toString(clone))
        val copy: InputStream = clone.copy()
        Assertions.assertEquals(line3, StreamUtils.toString(copy))
    }

    @Test
    @Throws(IOException::class)
    fun testWriteLength() {
        val source = of(line3)
        source.reset()
        val length = (source.available() / 2).toLong()
        val out = ByteArrayOutputStream()
        StreamUtils.write(source, out, length)
        Assertions.assertEquals(length, out.size().toLong())
    }
}
