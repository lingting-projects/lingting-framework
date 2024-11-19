package live.lingting.framework.http.body

import java.io.ByteArrayInputStream
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.nio.channels.WritableByteChannel
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import live.lingting.framework.stream.BytesInputStream

/**
 * @author lingting 2024-09-28 14:04
 */
class MemoryBody(private val bytes: ByteArray) : BodySource() {
    @JvmOverloads
    constructor(string: String, charset: Charset = StandardCharsets.UTF_8) : this(string.toByteArray(charset))

    constructor(stream: BytesInputStream) : this(stream.source())

    constructor(stream: InputStream) : this(stream.readAllBytes())

    override fun length(): Long {
        return bytes.size.toLong()
    }

    override fun bytes(): ByteArray? {
        return bytes
    }

    override fun openInput(): InputStream {
        return ByteArrayInputStream(bytes)
    }

    override fun string(charset: Charset): String {
        return String(bytes, charset)
    }


    override fun transferTo(output: OutputStream): Long {
        output.write(bytes)
        return bytes.size.toLong()
    }


    override fun transferTo(channel: WritableByteChannel): Long {
        channel.write(ByteBuffer.wrap(bytes))
        return bytes.size.toLong()
    }
}
