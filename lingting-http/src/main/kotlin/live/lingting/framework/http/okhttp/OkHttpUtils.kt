package live.lingting.framework.http.okhttp

import live.lingting.framework.http.HttpRequest
import live.lingting.framework.util.StringUtils
import okhttp3.Cookie.Builder.value
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.Buffer
import java.io.IOException
import java.io.InputStream

/**
 * @author lingting 2024-09-28 14:11
 */
class OkHttpUtils private constructor() {
    init {
        throw UnsupportedOperationException("This is a utility class and cannot be instantiated")
    }

    companion object {
        @Throws(IOException::class)
        fun input(body: RequestBody?): InputStream? {
            if (body == null) {
                return null
            }
            val buffer = Buffer()
            body.writeTo(buffer)
            return buffer.inputStream()
        }

        fun bytes(body: RequestBody?): ByteArray {
            if (body == null) {
                return ByteArray(0)
            }
            val buffer = Buffer()
            try {
                body.writeTo(buffer)
            } catch (e: IOException) {
                return ByteArray(0)
            }
            return buffer.readByteArray()
        }

        fun mediaType(body: HttpRequest.Body?): MediaType? {
            return mediaType((body?.contentType())!!)
        }

        fun mediaType(contentType: String): MediaType? {
            return if (StringUtils.hasText(contentType)) parse.parse(contentType) else null
        }
    }
}
