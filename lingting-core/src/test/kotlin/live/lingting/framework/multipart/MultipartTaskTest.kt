package live.lingting.framework.multipart

import java.io.ByteArrayOutputStream
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap
import live.lingting.framework.stream.BytesInputStream
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * @author lingting 2024-09-05 16:35
 */
class MultipartTaskTest {

    @Test
    fun test() {
        val source = "hello multipart test"
        val bytes = source.toByteArray()
        val input = BytesInputStream(bytes)

        val size = bytes.size.toLong()
        val partSize: Long = 3
        val number = Multipart.calculate(size, partSize)
        val multipart = Multipart.builder().source(input.copy()).partSize(partSize).build()

        assertEquals(number, multipart.parts.size.toLong())
        assertEquals(size, multipart.parts.sumOf(Part::size))
        for (part in multipart.parts) {
            assertEquals(part.size, part.end - part.start + 1)
        }
        val task = TestMultipartTask(multipart)
        assertFalse(task.isStarted)
        task.start().await(Duration.ofSeconds(5))
        assertTrue(task.isCompleted)
        val output = ByteArrayOutputStream(size.toInt())
        task.cache.keys.sorted().forEach { i -> output.write(task.cache[i]) }
        val merged = output.toByteArray()
        assertArrayEquals(bytes, merged)
        assertEquals(source, String(merged))

        val multipartLimit = Multipart.builder().source(input.copy()).partSize(partSize - 1).minPartSize(partSize).build()
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
