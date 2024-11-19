package live.lingting.framework.http

import java.io.InputStream
import java.net.Authenticator
import java.net.CookieManager
import java.net.CookiePolicy
import java.net.ProxySelector
import java.net.http.HttpRequest.BodyPublisher
import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpResponse.BodyHandlers
import java.time.Duration
import java.util.concurrent.ExecutorService
import java.util.function.Consumer
import java.util.function.Supplier
import live.lingting.framework.http.body.MemoryBody
import live.lingting.framework.http.header.HttpHeaders

/**
 * @author lingting 2024-09-02 15:33
 */
class JavaHttpClient(protected val client: java.net.http.HttpClient) : HttpClient() {
    override fun client(): java.net.http.HttpClient {
        return client
    }


    override fun request(request: HttpRequest): HttpResponse {
        val jr = convert(request)
        val r = client.send(jr, BodyHandlers.ofInputStream())
        return convert(request, r)
    }

    override fun request(request: HttpRequest, callback: ResponseCallback) {
        val jr = convert(request)
        client.sendAsync(jr, BodyHandlers.ofInputStream()).whenComplete { r: java.net.http.HttpResponse<InputStream>, throwable: Throwable? ->
            try {
                val response = convert(request, r)

                if (throwable != null) {
                    callback.onError(request, throwable)
                } else {
                    callback.onResponse(response)
                }
            } catch (e: Throwable) {
                callback.onError(request, e)
            }
        }
    }

    class Builder : HttpClient.Builder<JavaHttpClient, Builder?>() {
        protected var authenticator: Authenticator? = null

        fun authenticator(authenticator: Authenticator?): Builder {
            this.authenticator = authenticator
            return this
        }

        override fun infiniteTimeout(): Builder? {
            return timeout(null, null, null, null)
        }


        fun build(supplier: Supplier<java.net.http.HttpClient.Builder>): JavaHttpClient {
            val builder = supplier.get()

            if (trustManager != null) {
                val context = Https.sslContext(trustManager!!)
                builder.sslContext(context)
            }

            nonNull(connectTimeout, Consumer { duration: Duration? -> builder.connectTimeout(duration) })
            nonNull(proxySelector, Consumer { proxySelector: ProxySelector? -> builder.proxy(proxySelector) })
            nonNull(executor, Consumer { executor: ExecutorService? -> builder.executor(executor) })
            nonNull(authenticator, Consumer { authenticator: Authenticator? -> builder.authenticator(authenticator) })

            builder.followRedirects(if (redirects) java.net.http.HttpClient.Redirect.ALWAYS else java.net.http.HttpClient.Redirect.NEVER)

            if (cookie != null) {
                builder.cookieHandler(CookieManager(cookie, CookiePolicy.ACCEPT_ALL))
            }

            val delegate = builder.build()
            return JavaHttpClient(delegate)
        }

        override fun doBuild(): JavaHttpClient {
            return build { java.net.http.HttpClient.newBuilder() }
        }
    }

    companion object {
        fun convert(request: HttpRequest, r: java.net.http.HttpResponse<InputStream>): HttpResponse {
            val code = r.statusCode()
            val map = r.headers().map()
            val headers: HttpHeaders = HttpHeaders.Companion.of(map)
            val body = r.body()
            return HttpResponse(request, code, headers, body)
        }

        fun convert(request: HttpRequest): java.net.http.HttpRequest {
            val method = request.method()
            val uri = request.uri()
            val headers = request.headers()
            val body = request.body()

            val builder = java.net.http.HttpRequest.newBuilder().uri(uri)
            val publisher = convert(method!!, body)
            builder.method(method.name, publisher)
            headers!!.each { k: String, v: String? ->
                if (HttpClient.Companion.HEADERS_DISABLED.contains(k)) {
                    return@each
                }
                builder.header(k, v)
            }
            return builder.build()
        }

        fun convert(method: HttpMethod, body: HttpRequest.Body?): BodyPublisher {
            if (body == null || !method.allowBody()) {
                return BodyPublishers.noBody()
            }
            val source = body.source()
            if (source is MemoryBody) {
                return BodyPublishers.ofByteArray(source.bytes())
            }
            return BodyPublishers.ofInputStream { source!!.openInput() }
        }
    }
}
