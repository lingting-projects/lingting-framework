package live.lingting.framework.http.body

import java.io.ByteArrayInputStream
import java.io.InputStream
import java.io.OutputStream
import java.nio.channels.WritableByteChannel
import java.nio.charset.Charset

/**
 * @author lingting 2024-09-28 14:04
 */
object EmptyBody : MemoryBody(byteArrayOf()) {

    override fun length(): Long {
        return 0
    }

    override fun bytes(): ByteArray {
        return bytes
    }

    override fun openInput(): InputStream {
        return ByteArrayInputStream(bytes)
    }

    override fun string(charset: Charset): String {
        return ""
    }

    override fun transferTo(output: OutputStream): Long {
        return 0
    }

    override fun transferTo(channel: WritableByteChannel): Long {
        return 0
    }
}
