package live.lingting.framework.stream

import live.lingting.framework.util.FileUtils
import live.lingting.framework.util.StreamUtils
import java.io.File
import java.io.InputStream

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

    var isCloseAndDelete: Boolean = false


    protected fun getStream(): InputStream? {
        if (stream != null) {
            return stream
        }

        synchronized(lock) {
            if (stream != null) {
                return stream
            }
            stream = newStream()
        }
        return stream
    }


    protected abstract fun newStream(): InputStream


    override fun read(b: ByteArray): Int {
        return getStream()!!.read(b)
    }


    override fun read(b: ByteArray, off: Int, len: Int): Int {
        return getStream()!!.read(b, off, len)
    }


    override fun skip(n: Long): Long {
        return getStream()!!.skip(n)
    }


    override fun available(): Int {
        return getStream()!!.available()
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
        return getStream()!!.markSupported()
    }


    override fun read(): Int {
        return getStream()!!.read()
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
        val TEMP_DIR: File = FileUtils.createTempDir("clone")
    }
}
