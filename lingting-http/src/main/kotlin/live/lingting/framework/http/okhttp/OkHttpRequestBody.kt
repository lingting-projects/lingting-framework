package live.lingting.framework.http.okhttp

import live.lingting.framework.http.HttpRequest
import live.lingting.framework.http.body.BodySource
import live.lingting.framework.http.body.FileBody
import live.lingting.framework.http.body.MemoryBody
import live.lingting.framework.stream.FileCloneInputStream
import live.lingting.framework.util.StreamUtils
import okhttp3.Cookie.Builder.value
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.File
import java.io.IOException
import java.io.InputStream

/**
 * @author lingting 2024-09-02 16:20
 */
class OkHttpRequestBody(protected val source: BodySource?, protected val mediaType: MediaType?) : RequestBody() {
    constructor(file: File?) : this(FileCloneInputStream(file!!), MEDIA_STREAM)

    constructor(stream: InputStream) : this(stream, MEDIA_STREAM)

    constructor(input: InputStream, contentType: String) : this(input, OkHttpUtils.Companion.mediaType(contentType))

    constructor(input: InputStream, mediaType: MediaType?) : this(FileBody(input), mediaType)

    constructor(source: BodySource?, contentType: String?) : this(source, OkHttpUtils.Companion.mediaType(contentType!!))

    constructor(body: HttpRequest.Body) : this(body.source(), body.contentType())

    override fun contentType(): MediaType? {
        return mediaType
    }

    override fun contentLength(): Long {
        return source!!.length()
    }

    @Throws(IOException::class)
    override fun writeTo(bufferedSink: BufferedSink) {
        if (source is MemoryBody) {
            bufferedSink.write(source.bytes()!!)
            return
        }

        StreamUtils.read(source!!.openInput()) { buffer, len -> bufferedSink.write(buffer!!, 0, len!!) }
    }

    companion object {
        val MEDIA_STREAM: MediaType? = parse.parse("application/octet-stream")
    }
}
