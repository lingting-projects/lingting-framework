package live.lingting.framework.http

import live.lingting.framework.http.body.Body
import live.lingting.framework.http.body.MemoryBody
import live.lingting.framework.http.body.RequestBody
import live.lingting.framework.http.header.HttpHeaders
import live.lingting.framework.jackson.JacksonUtils
import java.net.URI
import java.util.function.Consumer

/**
 * @author lingting 2024-09-27 21:29
 */
open class HttpRequest private constructor(
    protected val method: HttpMethod,
    protected val uri: URI,
    protected val headers: HttpHeaders,
    protected val body: RequestBody
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

    fun body(): RequestBody {
        return body
    }

    override fun toString(): String {
        return "[$method] $uri"
    }

    class Builder {
        var method: HttpMethod = HttpMethod.GET
            private set

        var urlBuilder: HttpUrlBuilder = HttpUrlBuilder()
            private set

        val headers: HttpHeaders = HttpHeaders.empty()

        var body: RequestBody = RequestBody.empty()
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
            return url(HttpUrlBuilder.from(url))
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
        fun body(body: RequestBody): Builder {
            this.body = body
            val contentType = body.contentType()
            headers.contentType(contentType)
            return this
        }

        fun body(body: Body): Builder {
            return body(RequestBody.of(body, headers.contentType()))
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
                headers.host(urlBuilder.headerHost())
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

}
