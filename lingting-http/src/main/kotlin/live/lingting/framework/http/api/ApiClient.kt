package live.lingting.framework.http.api

import java.time.Duration
import live.lingting.framework.http.HttpClient
import live.lingting.framework.http.HttpRequest
import live.lingting.framework.http.HttpResponse
import live.lingting.framework.http.HttpUrlBuilder
import live.lingting.framework.http.body.BodySource
import live.lingting.framework.http.header.HttpHeaders
import live.lingting.framework.kt.logger
import live.lingting.framework.value.multi.StringMultiValue

/**
 * @author lingting 2024-09-14 15:33
 */
abstract class ApiClient<R : ApiRequest> protected constructor(@JvmField protected val host: String) {
    @JvmField
    val log = logger()

    @JvmField
    var client: HttpClient = CLIENT


    protected open fun customize(request: R) {
        //
    }

    protected fun customize(headers: HttpHeaders) {
        //
    }

    protected open fun customize(request: R, headers: HttpHeaders) {
        //
    }

    protected fun customize(builder: HttpUrlBuilder) {
        //
    }

    protected fun customize(request: R, builder: HttpRequest.Builder) {
        //
    }

    protected open fun customize(request: R, headers: HttpHeaders, source: BodySource, params: StringMultiValue) {
        //
    }

    protected abstract fun checkout(request: R, response: HttpResponse): HttpResponse


    protected fun call(r: R): HttpResponse {
        r.onCall()
        customize(r)

        val method = r.method()
        val headers: HttpHeaders = HttpHeaders.of(r.headers)
        val body = r.body()

        customize(headers)
        customize(r, headers)

        val path = r.path()
        r.onParams()
        val urlBuilder = HttpUrlBuilder.builder().https().host(host).uri(path).addParams(r.params)
        customize(urlBuilder)

        val builder: HttpRequest.Builder = HttpRequest.builder()
        val uri = urlBuilder.buildUri()
        builder.url(uri)
        headers.host(uri.host)

        customize(r, builder)
        customize(r, headers, body, urlBuilder.params())
        builder.headers(headers)
        builder.method(method.name).body(body)

        val request = builder.build()
        val response = client.request(request)
        return checkout(r, response)
    }

    companion object {
        @JvmField
        var CLIENT: HttpClient = HttpClient.builder()
            .disableSsl()
            .timeout(Duration.ofSeconds(15), Duration.ofSeconds(30))
            .build()

    }
}
