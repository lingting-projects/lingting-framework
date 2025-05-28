package live.lingting.framework.http.okhttp

import live.lingting.framework.http.body.Body
import live.lingting.framework.http.body.FileBody
import live.lingting.framework.http.body.MemoryBody
import live.lingting.framework.http.body.RequestBody
import live.lingting.framework.stream.FileCloneInputStream
import live.lingting.framework.util.StreamUtils
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okio.BufferedSink
import java.io.File
import java.io.InputStream

/**
 * @author lingting 2024-09-02 16:20
 */
open class OkHttpRequestBody(
    protected val source: Body,
    protected val mediaType: MediaType?
) : okhttp3.RequestBody() {

    companion object {

        val MEDIA_STREAM: MediaType = "application/octet-stream".toMediaTypeOrNull()!!

    }

    constructor(file: File) : this(FileCloneInputStream(file), MEDIA_STREAM)

    constructor(stream: InputStream) : this(stream, MEDIA_STREAM)

    constructor(input: InputStream, contentType: String?) : this(input, OkHttpUtils.mediaType(contentType))

    constructor(input: InputStream, mediaType: MediaType?) : this(FileBody(input), mediaType)

    constructor(source: Body, contentType: String?) : this(source, OkHttpUtils.mediaType(contentType))

    constructor(body: RequestBody) : this(body.source(), body.contentType())

    override fun contentType(): MediaType? {
        return mediaType
    }

    override fun contentLength(): Long {
        return source.length()
    }

    override fun writeTo(sink: BufferedSink) {
        if (source is MemoryBody) {
            sink.write(source.bytes())
            return
        }

        StreamUtils.read(source.openInput()) { buffer, len -> sink.write(buffer, 0, len) }
    }

}
