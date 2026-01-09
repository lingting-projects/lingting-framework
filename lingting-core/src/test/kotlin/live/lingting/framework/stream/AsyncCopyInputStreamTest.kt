package live.lingting.framework.stream

import live.lingting.framework.thread.Async
import live.lingting.framework.time.DateTime
import live.lingting.framework.util.DataSizeUtils.bytes
import live.lingting.framework.util.LocalDateTimeUtils.format
import live.lingting.framework.util.StreamUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.concurrent.atomic.AtomicLong

/**
 * @author lingting 2025/4/11 1:22
 */
class AsyncCopyInputStreamTest {

    @Test
    fun test() {
        val raw = DateTime.current().format()
        val array = raw.toByteArray()
        val source = BytesInputStream(array)
        assertEquals(raw, StreamUtils.toString(source))


        val error = AtomicLong(0)
        val async = Async(50)

        for (i in 0..300) {
            async.submit {
                val stream = AsyncCopyInputStream(source.copy())
                val bytes = StreamUtils.read(stream, ((i + 1) * 10).bytes)
                val text = bytes.toString(stream.charset)
                if (array.size != bytes.size || text != raw) {
                    error.incrementAndGet()
                }
            }
        }

        async.await()
        assertEquals(0, error.get())
    }


}
