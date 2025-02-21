package live.lingting.framework.http.api

import java.time.Duration
import live.lingting.framework.http.HttpClient
import live.lingting.framework.http.HttpRequest
import live.lingting.framework.http.HttpResponse
import live.lingting.framework.http.HttpUrlBuilder
import live.lingting.framework.http.body.BodySource
import live.lingting.framework.http.header.HttpHeaders
import live.lingting.framework.util.Slf4jUtils.logger

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

    protected open fun customize(headers: HttpHeaders) {
        //
    }

    protected open fun customize(request: R, headers: HttpHeaders) {
        //
    }

    protected open fun customize(builder: HttpUrlBuilder) {
        //
    }

    protected open fun customize(request: R, builder: HttpRequest.Builder) {
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

        val method = r.method()
        val headers: HttpHeaders = HttpHeaders.of(r.headers)
        val body = r.body()

        customize(headers)
        customize(r, headers)

        val path = r.path()
        r.onParams()
        val urlBuilder = urlBuilder().pathSegment(path).addParams(r.params)
        customize(urlBuilder)

        val builder = HttpRequest.builder()
        builder.url(urlBuilder)
        headers.host(urlBuilder.headerHost())

        customize(r, builder)
        customize(r, headers, body, urlBuilder)
        builder.headers(headers)
        builder.method(method.name).body(body)

        val request = builder.build()
        val response = client.request(request)
        return checkout(r, response)
    }

}
