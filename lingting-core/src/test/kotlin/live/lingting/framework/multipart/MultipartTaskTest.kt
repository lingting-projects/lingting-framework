package live.lingting.framework.multipart

import live.lingting.framework.stream.BytesInputStream
import live.lingting.framework.util.DataSizeUtils.bytes
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

/**
 * @author lingting 2024-09-05 16:35
 */
class MultipartTaskTest {

    @Test
    fun test() {
        val source = "hello multipart test"
        val bytes = source.toByteArray()
        val input = BytesInputStream(bytes)

        val size = bytes.size.bytes
        val partSize = 3.bytes
        val number = Multipart.calculate(size, partSize)
        val multipart = Multipart.builder().source(input.copy()).partSize(partSize).build()

        assertEquals(number, multipart.parts.size.toLong())
        assertEquals(size.bytes, multipart.parts.sumOf { it.size.bytes })
        for (part in multipart.parts) {
            assertEquals(part.size, part.end - part.start + 1)
        }
        val task = TestMultipartTask(multipart)
        assertFalse(task.isStarted)
        task.start().await(Duration.ofSeconds(5))
        assertTrue(task.isCompleted)
        val output = ByteArrayOutputStream(size.bytes.toInt())
        task.cache.keys.sorted().forEach { i -> output.write(task.cache.getValue(i)) }
        val merged = output.toByteArray()
        assertArrayEquals(bytes, merged)
        assertEquals(source, String(merged))

        val multipartLimit =
            Multipart.builder().source(input.copy()).partSize(partSize - 1).minPartSize(partSize).build()
        assertEquals(partSize, multipartLimit.partSize)
        assertEquals(number, multipartLimit.parts.size.toLong())
        input.close()
    }
}

class TestMultipartTask(multipart: Multipart) : MultipartTask<TestMultipartTask>(multipart) {
    val cache: MutableMap<Long, ByteArray> = ConcurrentHashMap()

    override fun onPart(part: Part) {
        multipart.stream(part).use { stream ->
            cache.put(part.index, stream.readAllBytes())
        }
    }
}
