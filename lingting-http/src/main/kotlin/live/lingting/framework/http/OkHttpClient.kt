package live.lingting.framework.http

import live.lingting.framework.flow.FutureSubscriber.get
import live.lingting.framework.function.ThrowingFunction
import live.lingting.framework.http.header.HttpHeaders
import live.lingting.framework.http.okhttp.OkHttpCookie
import live.lingting.framework.http.okhttp.OkHttpRequestBody
import live.lingting.framework.jackson.JacksonUtils
import live.lingting.framework.value.LazyValue.get
import okhttp3.Authenticator
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Cookie.Builder.value
import okhttp3.Dispatcher
import okhttp3.HttpUrl
import okhttp3.OkHttpClient.Builder.cookieJar
import okhttp3.OkHttpClient.Builder.followRedirects
import okhttp3.OkHttpClient.Builder.followSslRedirects
import okhttp3.OkHttpClient.Builder.sslSocketFactory
import okhttp3.Request
import okhttp3.Request.Builder.get
import okhttp3.Request.Builder.header
import okhttp3.Request.Builder.method
import okhttp3.Request.Builder.post
import okhttp3.Request.Builder.url
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException
import java.net.ProxySelector
import java.time.Duration
import java.util.function.Consumer
import java.util.function.Supplier
import javax.net.SocketFactory
import javax.net.ssl.HostnameVerifier

/**
 * @author lingting 2024-09-02 15:36
 */
class OkHttpClient(protected val client: okhttp3.OkHttpClient) : HttpClient() {
    override fun client(): okhttp3.OkHttpClient {
        return client
    }

    @Throws(IOException::class)
    override fun request(request: HttpRequest): HttpResponse {
        val okhttp = convert(request)

        request(okhttp).use { response ->
            return convert(request, response)
        }
    }

    @Throws(IOException::class)
    override fun request(request: HttpRequest, callback: ResponseCallback) {
        val okhttp = convert(request)
        request(okhttp, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onError(request, e)
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, r: Response) {
                try {
                    val response = convert(request, r)
                    callback.onResponse(response)
                } catch (e: Throwable) {
                    callback.onError(request, e)
                }
            }
        })
    }

    // region 原始请求
    @Throws(IOException::class)
    fun request(request: Request): Response {
        val call = client.newCall(request)
        return call.execute()
    }

    fun request(request: Request, callback: Callback) {
        val call = client.newCall(request)
        call.enqueue(callback)
    }


    @Throws(IOException::class)
    fun <T> request(request: Request, function: ThrowingFunction<Response?, T>): T {
        request(request).use { response ->
            return function.apply(response)
        }
    }

    @Throws(IOException::class)
    fun <T> request(request: Request, cls: Class<T>): T? {
        return request<T>(request, ThrowingFunction<Response, T> { response: Response ->
            val responseBody = response.body()
            if (responseBody == null) {
                return@request null
            }

            val string = responseBody!!.string()
            if (cls.isAssignableFrom(String::class.java)) {
                return@request string as T
            }
            JacksonUtils.toObj(string, cls)
        })
    }

    @Throws(IOException::class)
    fun get(url: String): Response {
        val builder: Builder = Builder().url(url).get()
        return request(builder.build())
    }

    @Throws(IOException::class)
    fun <T> get(url: String, cls: Class<T>): T {
        val builder: Builder = Builder().url(url).get()
        return request<T>(builder.build(), cls)
    }

    @Throws(IOException::class)
    fun get(url: HttpUrl): Response {
        val builder: Builder = Builder().url(url).get()
        return request(builder.build())
    }

    @Throws(IOException::class)
    fun <T> get(url: HttpUrl, cls: Class<T>): T {
        val builder: Builder = Builder().url(url).get()
        return request<T>(builder.build(), cls)
    }

    @Throws(IOException::class)
    fun post(url: String, body: RequestBody): Response {
        val builder: Builder = Builder().url(url).post(body)
        return request(builder.build())
    }

    @Throws(IOException::class)
    fun <T> post(url: String, requestBody: RequestBody, cls: Class<T>): T {
        val builder: Builder = Builder().url(url).post(requestBody)
        return request<T>(builder.build(), cls)
    }

    // endregion
    class Builder : HttpClient.Builder<OkHttpClient, Builder?>() {
        protected var authenticator: Authenticator? = null

        private var dispatcher: Dispatcher? = null

        fun authenticator(authenticator: Authenticator?): Builder {
            this.authenticator = authenticator
            return this
        }

        fun dispatcher(dispatcher: Dispatcher?): Builder {
            this.dispatcher = dispatcher
            return this
        }

        fun build(supplier: Supplier<Builder>): OkHttpClient {
            val builder = supplier.get()
            builder.followRedirects(redirects).followSslRedirects(redirects)
            nonNull(socketFactory, Consumer { socketFactory: SocketFactory? -> builder.socketFactory(socketFactory) })
            nonNull(hostnameVerifier, Consumer { hostnameVerifier: HostnameVerifier? -> builder.hostnameVerifier(hostnameVerifier) })

            if (sslContext != null && trustManager != null) {
                builder.sslSocketFactory(sslContext!!.socketFactory, trustManager)
            }

            nonNull(callTimeout, Consumer { duration: Duration? -> builder.callTimeout(duration) })
            nonNull(connectTimeout, Consumer { duration: Duration? -> builder.connectTimeout(duration) })
            nonNull(readTimeout, Consumer { duration: Duration? -> builder.readTimeout(duration) })
            nonNull(writeTimeout, Consumer { duration: Duration? -> builder.writeTimeout(duration) })
            nonNull(proxySelector, Consumer { proxySelector: ProxySelector? -> builder.proxySelector(proxySelector) })
            nonNull(authenticator, Consumer { authenticator: Authenticator? -> builder.authenticator(authenticator) })

            if (cookie != null) {
                builder.cookieJar(OkHttpCookie(cookie!!))
            }

            if (dispatcher == null && executor != null) {
                dispatcher = Dispatcher(executor)
            }

            nonNull(dispatcher, Consumer { dispatcher: Dispatcher? -> builder.dispatcher(dispatcher) })

            val client: okhttp3.OkHttpClient = builder.build()
            return OkHttpClient(client)
        }

        override fun doBuild(): OkHttpClient {
            return build { Builder() }
        }
    }

    companion object {
        @Throws(IOException::class)
        fun convert(request: HttpRequest): Request {
            val method = request.method()
            val uri = request.uri()
            val headers = request.headers()
            val body = request.body()

            val builder = Builder()
            // 请求头
            headers!!.each { k: String, v: String? ->
                if (HttpClient.Companion.HEADERS_DISABLED.contains(k)) {
                    return@each
                }
                builder.header(k, v)
            }
            // 请求地址
            builder.url(uri!!.toURL())
            builder.method(method!!.name, if (method.allowBody()) OkHttpRequestBody(body!!) else null)
            return builder.build()
        }

        @Throws(IOException::class)
        fun convert(request: HttpRequest, response: Response): HttpResponse {
            val code = response.code()
            val body = response.body()
            val stream = wrap(body?.byteStream())
            val map: Map<String?, List<String?>?> = response.headers().toMultimap()
            val headers: HttpHeaders = HttpHeaders.Companion.of(map)
            return HttpResponse(request, code, headers, stream)
        }
    }
}
