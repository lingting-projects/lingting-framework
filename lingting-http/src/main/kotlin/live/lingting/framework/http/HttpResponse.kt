package live.lingting.framework.http

import com.fasterxml.jackson.core.type.TypeReference
import live.lingting.framework.http.header.HttpHeaders
import live.lingting.framework.jackson.JacksonUtils
import live.lingting.framework.lock.JavaReentrantLock
import live.lingting.framework.lock.LockRunnable
import live.lingting.framework.stream.CloneInputStream
import live.lingting.framework.util.StreamUtils
import okhttp3.Cookie.Builder.value
import java.io.Closeable
import java.io.IOException
import java.io.InputStream
import java.lang.reflect.Type
import java.net.URI
import java.util.function.Function

/**
 * @author lingting 2024-09-12 23:37
 */
class HttpResponse(protected val request: HttpRequest, protected val code: Int, protected val headers: HttpHeaders, protected val body: InputStream) : Closeable {
    protected val lock: JavaReentrantLock = JavaReentrantLock()

    protected var string: String? = null

    fun request(): HttpRequest {
        return request
    }

    fun uri(): URI? {
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

    @Throws(IOException::class)
    fun bytes(): ByteArray {
        body().use { `in` ->
            return `in`.readAllBytes()
        }
    }


    fun string(): String? {
        if (string != null) {
            return string
        }

        lock.runByInterruptibly(object : LockRunnable {
            @Throws(InterruptedException::class)
            override fun run() {
                if (string != null) {
                    return
                }
                body().use { stream ->
                    string = if (stream == null) "" else StreamUtils.toString(stream)
                }
            }
        })
        return string
    }

    fun <T> convert(cls: Class<T>?): T {
        val json = string()
        return JacksonUtils.toObj(json!!, cls)
    }

    fun <T> convert(type: Type?): T {
        val json = string()
        return JacksonUtils.toObj(json!!, type)
    }

    fun <T> convert(reference: TypeReference<T>?): T {
        val json = string()
        return JacksonUtils.toObj(json, reference)
    }

    fun <T> convert(function: Function<String?, T>): T {
        val json = string()
        return function.apply(json)
    }

    fun isRange(start: Int, end: Int): Boolean {
        val status = code()
        return status >= start && status <= end
    }

    fun is2xx(): Boolean {
        return isRange(200, 299)
    }

    @Throws(IOException::class)
    override fun close() {
        if (body is CloneInputStream) {
            body.isCloseAndDelete = true
        }
        StreamUtils.close(body)
    }
}
