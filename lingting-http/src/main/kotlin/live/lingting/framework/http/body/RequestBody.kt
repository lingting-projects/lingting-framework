package live.lingting.framework.http.body

import java.io.InputStream
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

/**
 * @author lingting 2024-09-28 11:54
 */
class RequestBody(private val source: Body, private val contentType: String?) {

    companion object {

        fun empty(): RequestBody {
            return RequestBody(Body.empty(), null)
        }

        fun of(body: Body): RequestBody {
            return RequestBody(body, null)
        }

        fun of(body: Body, contentType: String?): RequestBody {
            return RequestBody(body, contentType)
        }

    }

    fun contentType(): String? {
        return contentType
    }

    fun contentLength(): Long {
        return source.length()
    }

    fun source(): Body {
        return source
    }

    fun bytes(): ByteArray {
        return source.bytes()
    }

    fun input(): InputStream {
        return source.openInput()
    }

    @JvmOverloads
    fun string(charset: Charset = StandardCharsets.UTF_8): String {
        return source.string(charset)
    }

}
