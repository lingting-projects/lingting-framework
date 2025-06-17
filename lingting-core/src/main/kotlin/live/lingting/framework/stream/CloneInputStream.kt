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

    companion object {
        @JvmField
        val TEMP_DIR: File = FileUtils.createTempDir("clone")
    }

    protected val stream by lazy { newStream() }

    var isCloseAndDelete: Boolean = false

    protected abstract fun newStream(): InputStream

    override fun read(b: ByteArray): Int {
        return stream.read(b)
    }

    override fun read(b: ByteArray, off: Int, len: Int): Int {
        return stream.read(b, off, len)
    }

    open fun readAllBytes(): ByteArray {
        return StreamUtils.read(this)
    }

    override fun skip(n: Long): Long {
        return stream.skip(n)
    }

    override fun available(): Int {
        return stream.available()
    }

    override fun close() {
        StreamUtils.close(stream)
        if (isCloseAndDelete) {
            clear()
        }
    }

    override fun mark(limit: Int) {
        stream.mark(limit)
    }

    override fun reset() {
        stream.reset()
    }

    override fun markSupported(): Boolean {
        return stream.markSupported()
    }

    override fun read(): Int {
        return stream.read()
    }

    fun size(): Long {
        return size
    }

    open fun source(): Any {
        return source
    }

    abstract fun copy(): CloneInputStream

    abstract fun clear()

}
