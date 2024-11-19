package live.lingting.framework.http.body

import java.io.InputStream
import java.io.OutputStream
import java.nio.channels.WritableByteChannel
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import live.lingting.framework.stream.BytesInputStream

/**
 * @author lingting 2024-09-28 14:04
 */
abstract class BodySource {

    abstract fun length(): Long

    abstract fun bytes(): ByteArray

    abstract fun openInput(): InputStream

    fun string(): String {
        return string(StandardCharsets.UTF_8)
    }

    abstract fun string(charset: Charset): String

    abstract fun transferTo(output: OutputStream): Long

    abstract fun transferTo(channel: WritableByteChannel): Long

    companion object {
        @JvmStatic
        fun empty(): MemoryBody {
            return EmptyBody
        }

        @JvmStatic
        fun of(stream: InputStream): BodySource {
            if (stream is BytesInputStream) {
                return MemoryBody(stream.source())
            }
            return FileBody(stream)
        }

    }
}
