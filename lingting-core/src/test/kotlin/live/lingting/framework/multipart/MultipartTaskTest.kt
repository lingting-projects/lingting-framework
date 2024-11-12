package live.lingting.framework.multipart

import live.lingting.framework.retry.Retry.value
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap


/**
 * @author lingting 2024-09-05 16:35
 */
internal class MultipartTaskTest {
    @Test
    @Throws(IOException::class)
    fun test() {
        val source = "hello multipart test"
        val bytes = source.toByteArray()
        val input: InputStream = ByteArrayInputStream(bytes)

        val size = bytes.size.toLong()
        val partSize: Long = 3
        val number = Multipart.calculate(size, partSize)
        val multipart = Multipart.builder().source(input).partSize(partSize).build()

        Assertions.assertEquals(number, multipart.parts.size.toLong())
        Assertions.assertEquals(size, multipart.parts.stream().mapToLong(Part::size).sum())
        for (part in multipart.parts) {
            Assertions.assertEquals(part.size, part.end - part.start + 1)
        }
        val task = TestMultipartTask(multipart)
        Assertions.assertFalse(task.isStarted)
        task.start()!!.await(Duration.ofSeconds(5))
        Assertions.assertTrue(task.isCompleted)
        val output = ByteArrayOutputStream(size.toInt())
        task.cache.keys.stream().sorted().forEach { i -> output.write(task.cache[i]) }
        val merged = output.toByteArray()
        Assertions.assertArrayEquals(bytes, merged)
        Assertions.assertEquals(source, String(merged))
    }
}

internal class TestMultipartTask(multipart: Multipart) : MultipartTask<TestMultipartTask?>(multipart) {
    val cache: MutableMap<Long, ByteArray> = ConcurrentHashMap()

    override fun onPart(part: Part) {
        multipart.stream(part).use { stream ->
            cache.put(part.index, stream.readAllBytes())
        }
    }
}
