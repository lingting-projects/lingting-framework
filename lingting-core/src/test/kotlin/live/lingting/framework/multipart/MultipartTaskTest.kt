package live.lingting.framework.multipart

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * @author lingting 2024-09-05 16:35
 */
internal class MultipartTaskTest {
    @Test

    fun test() {
        val source = "hello multipart test"
        val bytes = source.toByteArray()
        val input: InputStream = ByteArrayInputStream(bytes)

        val size = bytes.size.toLong()
        val partSize: Long = 3
        val number = Multipart.calculate(size, partSize)
        val multipart = Multipart.builder().source(input).partSize(partSize).build()

        assertEquals(number, multipart.parts.size.toLong())
        assertEquals(size, multipart.parts.stream().mapToLong(Part::size).sum())
        for (part in multipart.parts) {
            assertEquals(part.size, part.end - part.start + 1)
        }
        val task = TestMultipartTask(multipart)
        assertFalse(task.isStarted)
        task.start().await(Duration.ofSeconds(5))
        assertTrue(task.isCompleted)
        val output = ByteArrayOutputStream(size.toInt())
        task.cache.keys.stream().sorted().forEach { i -> output.write(task.cache[i]) }
        val merged = output.toByteArray()
        assertArrayEquals(bytes, merged)
        assertEquals(source, String(merged))
    }
}

internal class TestMultipartTask(multipart: Multipart) : MultipartTask<TestMultipartTask>(multipart) {
    val cache: MutableMap<Long, ByteArray> = ConcurrentHashMap()

    override fun onPart(part: Part) {
        multipart.stream(part).use { stream ->
            cache.put(part.index, stream.readAllBytes())
        }
    }
}
