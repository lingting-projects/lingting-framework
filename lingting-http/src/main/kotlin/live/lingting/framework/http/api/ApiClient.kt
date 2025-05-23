package live.lingting.framework.http.api

import live.lingting.framework.http.HttpClient
import live.lingting.framework.http.HttpRequest
import live.lingting.framework.http.HttpResponse
import live.lingting.framework.http.HttpUrlBuilder
import live.lingting.framework.http.body.BodySource
import live.lingting.framework.http.header.HttpHeaders
import live.lingting.framework.util.Slf4jUtils.logger
import java.time.Duration

/**
 * @author lingting 2024-09-14 15:33
 */
abstract class ApiClient<R : ApiRequest> @JvmOverloads protected constructor(
    @JvmField protected val host: String,
    @JvmField protected val ssl: Boolean = true,
) {

    companion object {

        @JvmStatic
        var defaultClient: HttpClient = HttpClient.builder()
            .disableSsl()
            .callTimeout(Duration.ofSeconds(10))
            .connectTimeout(Duration.ofSeconds(15))
            .readTimeout(Duration.ofSeconds(30))
            .build()

    }

    @JvmField
    val log = logger()

    @JvmField
    var client: HttpClient = defaultClient

    protected open fun customize(request: R) {
        //
    }

    protected open fun customize(body: BodySource) {
        //
    }

    protected open fun customize(headers: HttpHeaders) {
        //
    }

    protected open fun customize(body: BodySource, headers: HttpHeaders) {
        //
    }

    protected open fun customize(request: R, headers: HttpHeaders) {
        //
    }

    protected open fun customize(builder: HttpUrlBuilder) {
        //
    }

    protected open fun customize(request: R, headers: HttpHeaders, source: BodySource, url: HttpUrlBuilder) {
        //
    }

    protected abstract fun checkout(request: R, response: HttpResponse): HttpResponse

    protected open fun urlBuilder(): HttpUrlBuilder {
        return HttpUrlBuilder.builder().let {
            if (ssl) {
                it.https()
            } else {
                it.http()
            }
        }.host(host)
    }

    protected open fun call(r: R): HttpResponse {
        r.onCall()
        customize(r)

        val headers = HttpHeaders.of(r.headers)
        val body = r.body()

        customize(body)
        customize(headers)
        customize(r, headers)
        customize(body, headers)

        val path = r.path()
        r.onParams()
        val urlBuilder = urlBuilder().pathSegment(path).addParams(r.params)
        customize(urlBuilder)

        headers.host(urlBuilder.headerHost())

        customize(r, headers, body, urlBuilder)
        val response = call(urlBuilder, r, headers, body)
        return checkout(r, response)
    }

    protected open fun call(urlBuilder: HttpUrlBuilder, r: R, headers: HttpHeaders, body: BodySource): HttpResponse {
        val request = buildRequest(urlBuilder, headers, r, body)
        return call(r, request)
    }

    protected open fun call(r: R, request: HttpRequest): HttpResponse {
        return client.request(request)
    }

    protected open fun buildRequest(
        urlBuilder: HttpUrlBuilder,
        headers: HttpHeaders,
        r: R,
        body: BodySource
    ): HttpRequest {
        val builder = HttpRequest.builder()
        builder.url(urlBuilder)
        builder.headers(headers)
        builder.method(r.method().name).body(body)
        val request = builder.build()
        return request
    }

}
