package live.lingting.framework.http.body

import live.lingting.framework.stream.FileCloneInputStream
import live.lingting.framework.util.StreamUtils
import okhttp3.Cookie.Builder.value
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.nio.channels.WritableByteChannel
import java.nio.charset.Charset

/**
 * @author lingting 2024-09-28 14:04
 */
class FileBody(private val input: FileCloneInputStream) : BodySource() {
    constructor(input: InputStream) : this(FileCloneInputStream(input))

    override fun length(): Long {
        return input.size()
    }


    override fun bytes(): ByteArray? {
        return input.copy().readAllBytes()
    }

    override fun openInput(): InputStream {
        return input.copy()
    }


    override fun string(charset: Charset): String {
        return StreamUtils.toString(input.copy(), charset)
    }

    @Throws(IOException::class)
    override fun transferTo(output: OutputStream): Long {
        StreamUtils.write(input.copy(), output)
        return input.size()
    }

    @Throws(IOException::class)
    override fun transferTo(channel: WritableByteChannel): Long {
        StreamUtils.read(input.copy()) { bytes, len -> channel.write(ByteBuffer.wrap(bytes, 0, len!!)) }
        return input.size()
    }
}
