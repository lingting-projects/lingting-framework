package live.lingting.framework.stream

import java.io.File
import java.io.InputStream
import live.lingting.framework.util.FileUtils
import live.lingting.framework.util.StreamUtils

/**
 * @author lingting 2024/10/24 11:00
 */
abstract class CloneInputStream(
    protected val source: Any,
    /**
     * 字节数
     */
    protected val size: Long
) : InputStream() {
    protected val lock: Any = ""

    protected var stream: InputStream? = null
        get() {
            if (field != null) {
                return field
            }

            synchronized(lock) {
                if (field != null) {
                    return field
                }
                field = newStream()
            }
            return field
        }

    var isCloseAndDelete: Boolean = false

    protected abstract fun newStream(): InputStream

    override fun read(b: ByteArray): Int {
        return stream!!.read(b)
    }

    override fun read(b: ByteArray, off: Int, len: Int): Int {
        return stream!!.read(b, off, len)
    }

    override fun skip(n: Long): Long {
        return stream!!.skip(n)
    }

    override fun available(): Int {
        return stream!!.available()
    }

    override fun close() {
        StreamUtils.close(stream)
        if (isCloseAndDelete) {
            clear()
        }
    }

    override fun mark(limit: Int) {
        if (stream != null) {
            stream!!.mark(limit)
        }
    }

    override fun reset() {
        if (stream != null) {
            stream!!.reset()
        }
    }

    override fun markSupported(): Boolean {
        return stream!!.markSupported()
    }

    override fun read(): Int {
        return stream!!.read()
    }

    fun size(): Long {
        return size
    }

    open fun source(): Any {
        return source
    }

    abstract fun copy(): CloneInputStream

    abstract fun clear()

    companion object {
        @JvmField
        val TEMP_DIR: File = FileUtils.createTempDir("clone")
    }
}
