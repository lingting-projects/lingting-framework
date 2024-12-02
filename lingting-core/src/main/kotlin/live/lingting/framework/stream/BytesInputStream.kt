package live.lingting.framework.stream

import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import live.lingting.framework.util.StreamUtils

/**
 * @author lingting 2024/10/24 10:59
 */
class BytesInputStream(source: ByteArray) : CloneInputStream(source, source.size.toLong()) {

    constructor(source: File) : this(FileInputStream(source))

    constructor(input: BytesInputStream) : this(input.source())

    constructor(input: InputStream) : this(StreamUtils.read(input))

    override fun readAllBytes(): ByteArray {
        return source()
    }

    override fun newStream(): InputStream {
        return ByteArrayInputStream(source())
    }

    override fun copy(): BytesInputStream {
        return BytesInputStream(this)
    }

    override fun source(): ByteArray {
        return source as ByteArray
    }

    override fun clear() {
        //
    }
}
