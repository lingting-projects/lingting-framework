package live.lingting.framework.http

import java.io.InputStream
import java.net.URI
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.function.Consumer
import live.lingting.framework.http.body.BodySource
import live.lingting.framework.http.body.MemoryBody
import live.lingting.framework.http.header.HttpHeaders
import live.lingting.framework.jackson.JacksonUtils

/**
 * @author lingting 2024-09-27 21:29
 */
class HttpRequest private constructor(
    protected val method: HttpMethod,
    protected val uri: URI,
    protected val headers: HttpHeaders,
    protected val body: Body
) {

    companion object {
        @JvmStatic
        fun builder(): Builder {
            return Builder()
        }
    }

    fun method(): HttpMethod {
        return method
    }

    fun uri(): URI {
        return uri
    }

    fun headers(): HttpHeaders {
        return headers
    }

    fun body(): Body {
        return body
    }

    class Builder {
        var method: HttpMethod = HttpMethod.GET
            private set

        var urlBuilder: HttpUrlBuilder = HttpUrlBuilder()
            private set

        val headers: HttpHeaders = HttpHeaders.empty()

        var body: Body = Body.empty()
            private set

        // region method
        fun method(method: HttpMethod): Builder {
            this.method = method
            return this
        }

        fun method(method: String): Builder {
            return method(HttpMethod.valueOf(method))
        }

        fun get(): Builder {
            return method(HttpMethod.GET)
        }

        fun put(): Builder {
            return method(HttpMethod.PUT)
        }

        fun post(): Builder {
            return method(HttpMethod.POST)
        }

        fun delete(): Builder {
            return method(HttpMethod.DELETE)
        }

        fun head(): Builder {
            return method(HttpMethod.HEAD)
        }

        fun options(): Builder {
            return method(HttpMethod.OPTIONS)
        }

        fun patch(): Builder {
            return method(HttpMethod.PATCH)
        }

        // endregion

        // region url
        fun url(url: String): Builder {
            return url(URI.create(url))
        }

        fun url(url: URI): Builder {
            this.urlBuilder = HttpUrlBuilder.from(url)
            return this
        }

        fun url(consumer: Consumer<HttpUrlBuilder>): Builder {
            consumer.accept(urlBuilder)
            return this
        }

        fun url(urlBuilder: HttpUrlBuilder): Builder {
            this.urlBuilder = urlBuilder
            return this
        }

        // endregion

        // region headers
        fun header(name: String, value: String): Builder {
            headers.add(name, value)
            return this
        }

        fun headers(name: String, values: Collection<String>): Builder {
            headers.addAll(name, values)
            return this
        }

        fun headers(map: Map<String, Collection<String>>): Builder {
            headers.addAll(map)
            return this
        }

        fun headers(headers: HttpHeaders): Builder {
            this.headers.addAll(headers)
            return this
        }

        fun headers(consumer: Consumer<HttpHeaders>): Builder {
            consumer.accept(headers)
            return this
        }

        // endregion

        // region body
        fun body(body: Body): Builder {
            this.body = body
            val contentType = body.contentType()
            headers.contentType(contentType)
            return this
        }

        fun body(body: BodySource): Builder {
            return body(Body.of(body, headers.contentType()))
        }

        fun body(body: String): Builder {
            return body(MemoryBody(body))
        }

        fun jsonBody(obj: Any): Builder {
            return body(JacksonUtils.toJson(obj))
        }

        // endregion

        fun build(): HttpRequest {
            val requestBody = body
            // 不覆盖已有host
            val host = headers.host()
            if (host.isNullOrBlank()) {
                urlBuilder.host(urlBuilder.headerHost())
            }
            // 不覆盖已有contentType
            val contentType = headers.contentType()
            if (contentType.isNullOrBlank()) {
                val type = requestBody.contentType()
                if (!type.isNullOrBlank()) {
                    headers.contentType(type)
                }
            }
            val uri = urlBuilder.buildUri()
            return HttpRequest(method, uri, headers.unmodifiable(), requestBody)
        }
    }

    /**
     * @author lingting 2024-09-28 11:54
     */
    class Body(private val source: BodySource, private val contentType: String?) {
        fun contentType(): String? {
            return contentType
        }

        fun contentLength(): Long {
            return source.length()
        }

        fun source(): BodySource {
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

        companion object {
            fun empty(): Body {
                return Body(BodySource.empty(), null)
            }

            fun of(body: BodySource): Body {
                return Body(body, null)
            }

            fun of(body: BodySource, contentType: String?): Body {
                return Body(body, contentType)
            }
        }
    }

}
