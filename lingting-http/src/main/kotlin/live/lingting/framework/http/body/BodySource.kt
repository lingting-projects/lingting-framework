package live.lingting.framework.http.body

import live.lingting.framework.stream.BytesInputStream
import okhttp3.Cookie.Builder.value
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.nio.channels.WritableByteChannel
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

/**
 * @author lingting 2024-09-28 14:04
 */
abstract class BodySource {
    abstract fun length(): Long

    abstract fun bytes(): ByteArray?

    abstract fun openInput(): InputStream

    fun string(): String {
        return string(StandardCharsets.UTF_8)
    }

    abstract fun string(charset: Charset): String

    @Throws(IOException::class)
    abstract fun transferTo(output: OutputStream): Long

    @Throws(IOException::class)
    abstract fun transferTo(channel: WritableByteChannel): Long

    companion object {
        fun empty(): MemoryBody {
            return MemoryBody(ByteArray(0))
        }

        @JvmStatic
        @Throws(IOException::class)
        fun of(stream: InputStream): BodySource {
            if (stream is BytesInputStream) {
                return MemoryBody(stream.source())
            }
            return FileBody(stream)
        }
    }
}
