package live.lingting.framework.stream

import live.lingting.framework.function.StateKeepRunnable
import live.lingting.framework.lock.JavaReentrantLock
import live.lingting.framework.util.DurationUtils.minutes
import live.lingting.framework.util.Slf4jUtils.logger
import live.lingting.framework.util.StreamUtils
import live.lingting.framework.util.ThreadUtils
import java.io.InputStream
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicLong

/**
 * @author lingting 2025/4/10 20:59
 */
class AsyncCopyInputStream(
    private val source: InputStream,
    val charset: Charset = StandardCharsets.UTF_8,
) : InputStream() {

    companion object {
        private val log = logger()
        private val counter = AtomicLong()
    }

    private val queue = LinkedBlockingQueue<Byte>()

    private val lock = JavaReentrantLock()

    private val r = object : StateKeepRunnable("aci-${counter.andIncrement}") {

        override fun doProcess() {
            try {
                StreamUtils.read(source) { bytes, len ->
                    if (len > 0) {
                        lock.runByInterruptibly {
                            for (i in 0..(len - 1)) {
                                queue.add(bytes[i])
                            }
                            lock.signal()
                        }
                    }
                }
            } catch (e: Exception) {
                AsyncCopyInputStream.log.error("异步复制流时异常!", e)
            } finally {
                lock.runByTry { lock.signalAll() }
            }
        }

    }.also { ThreadUtils.execute(it) }

    @Suppress("kotlin:S3776")
    override fun read(bytes: ByteArray, off: Int, len: Int): Int {
        var pos = 0
        var stop: Boolean

        do {
            stop = lock.getByInterruptibly {
                while (true) {
                    val b = queue.poll()
                    if (b == null) {
                        if (pos < 1 && !r.isFinish) {
                            lock.await(1.minutes)
                            return@getByInterruptibly false
                        }
                        break
                    }
                    bytes[off + pos] = b
                    pos += 1
                    if (pos == len) {
                        return@getByInterruptibly true
                    }
                }
                pos > 0 || r.isFinish
            }
        } while (!stop)

        if (r.isFinish && pos < 1) {
            return -1
        }
        return pos
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
