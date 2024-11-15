package live.lingting.framework.http.api

import live.lingting.framework.http.HttpClient
import live.lingting.framework.http.HttpRequest
import live.lingting.framework.http.HttpResponse
import live.lingting.framework.http.HttpUrlBuilder
import live.lingting.framework.http.body.BodySource
import live.lingting.framework.http.header.HttpHeaders
import live.lingting.framework.kt.logger
import live.lingting.framework.value.multi.StringMultiValue
import java.time.Duration

/**
 * @author lingting 2024-09-14 15:33
 */
abstract class ApiClient<R : ApiRequest?> protected constructor(@JvmField protected val host: String) {
    @JvmField
    val log = logger()

    protected var client: HttpClient = defaultClient

    protected open fun customize(request: R) {
        //
    }

    protected fun customize(headers: HttpHeaders?) {
        //
    }

    protected open fun customize(request: R, headers: HttpHeaders?) {
        //
    }

    protected fun customize(builder: HttpUrlBuilder?) {
        //
    }

    protected fun customize(request: R, builder: HttpRequest.Builder?) {
        //
    }

    protected open fun customize(request: R, headers: HttpHeaders?, source: BodySource?, params: StringMultiValue?) {
        //
    }

    protected abstract fun checkout(request: R, response: HttpResponse?): HttpResponse


    protected fun call(r: R): HttpResponse {
        r!!.onCall()
        customize(r)

        val method = r.method()
        val headers: HttpHeaders = HttpHeaders.Companion.of(r.getHeaders())
        val body = r.body()

        customize(headers)
        customize(r, headers)

        val path = r.path()
        r.onParams()
        val urlBuilder = HttpUrlBuilder.builder().https().host(host).uri(path!!).addParams(r.getParams())
        customize(urlBuilder)

        val builder: HttpRequest.Builder = HttpRequest.Companion.builder()
        val uri = urlBuilder.buildUri()
        builder.url(uri)
        headers.host(uri.host)

        customize(r, builder)
        customize(r, headers, body, urlBuilder.params())
        builder.headers(headers)
        builder.method(method!!.name).body(body)

        val request = builder.build()
        val response = client.request(request)
        return checkout(r, response)
    }

    fun setClient(client: HttpClient) {
        this.client = client
    }

    companion object {
        var defaultClient: HttpClient = HttpClient.Companion.okhttp()
            .disableSsl()
            .timeout(Duration.ofSeconds(15), Duration.ofSeconds(30))
            .build()

        fun setDefaultClient(defaultClient: HttpClient) {
            Companion.defaultClient = defaultClient
        }
    }
}
