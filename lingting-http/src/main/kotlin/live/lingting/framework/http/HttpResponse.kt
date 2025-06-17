package live.lingting.framework.http

import com.fasterxml.jackson.core.type.TypeReference
import java.io.Closeable
import java.io.InputStream
import java.lang.reflect.Type
import java.net.URI
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.function.Function
import live.lingting.framework.http.header.HttpHeaders
import live.lingting.framework.jackson.JacksonUtils
import live.lingting.framework.stream.CloneInputStream
import live.lingting.framework.util.StreamUtils
import live.lingting.framework.util.StreamUtils.readAllBytes

/**
 * @author lingting 2024-09-12 23:37
 */
open class HttpResponse(
    protected val request: HttpRequest,
    protected val code: Int,
    protected val headers: HttpHeaders,
    protected val body: InputStream
) : Closeable {

    protected val string by lazy { String(bytes, charset) }

    protected val bytes: ByteArray by lazy { body().use { it.readAllBytes() } }

    val is2xx = code().let { it >= 200 && it <= 299 }

    val isOk = is2xx

    val charset: Charset by lazy {
        headers.charset()?.let {
            try {
                Charset.forName(it)
            } catch (_: Exception) {
                null
            }
        } ?: StandardCharsets.UTF_8
    }

    fun request(): HttpRequest {
        return request
    }

    fun uri(): URI {
        return request.uri()
    }

    fun code(): Int {
        return code
    }

    fun headers(): HttpHeaders {
        return headers
    }

    fun body(): InputStream {
        return body
    }

    fun bytes(): ByteArray {
        return bytes
    }

    fun string(): String {
        return string
    }

    fun <T> convert(cls: Class<T>): T {
        val json = string()
        return JacksonUtils.toObj(json, cls)
    }

    fun <T> convert(type: Type): T {
        val json = string()
        return JacksonUtils.toObj(json, type)
    }

    fun <T> convert(reference: TypeReference<T>): T {
        val json = string()
        return JacksonUtils.toObj(json, reference)
    }

    fun <T> convert(function: Function<String, T>): T {
        val json = string()
        return function.apply(json)
    }

    override fun close() {
        if (body is CloneInputStream) {
            body.isCloseAndDelete = true
        }
        StreamUtils.close(body)
    }

    override fun toString(): String {
        return "[$code] ${request.uri()}"
    }

}
