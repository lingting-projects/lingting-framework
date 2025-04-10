package live.lingting.framework.stream

import live.lingting.framework.function.StateKeepRunnable
import live.lingting.framework.util.DurationUtils.millis
import live.lingting.framework.util.Slf4jUtils.logger
import live.lingting.framework.util.StreamUtils
import live.lingting.framework.util.ThreadUtils
import java.io.InputStream
import java.nio.charset.Charset
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicLong

/**
 * @author lingting 2025/4/10 20:59
 */
class AsyncCopyInputStream(
    private val source: InputStream,
    val charset: Charset,
) : InputStream() {

    companion object {
        private val log = logger()
        private val counter = AtomicLong()
    }

    private val queue = ConcurrentLinkedQueue<Byte>()

    private val r = object : StateKeepRunnable("aci-${counter.andIncrement}") {

        override fun doProcess() {
            try {
                StreamUtils.read(source) { bytes, len ->
                    for (i in 0..len) {
                        queue.add(bytes[i])
                    }
                }
            } catch (e: Exception) {
                AsyncCopyInputStream.log.error("异步复制流时异常!", e)
            }
        }

    }.also { ThreadUtils.execute(it) }

    override fun read(bytes: ByteArray, off: Int, len: Int): Int {
        var pos = 0

        while (true) {
            val b = queue.poll()
            if (b == null) {
                if (r.isFinish) {
                    return -1
                }
                if (pos > 0) {
                    return pos
                }
                Thread.sleep(10.millis)
                continue
            }
            bytes[off + pos] = b
            pos += 1
        }
    }

    override fun read(): Int {
        val bytes = ByteArray(1)
        val i = read(bytes)
        if (i < 1) {
            return -1
        }
        return bytes[0].toInt()
    }

}
