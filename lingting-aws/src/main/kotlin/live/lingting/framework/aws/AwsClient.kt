package live.lingting.framework.aws

import live.lingting.framework.aws.exception.AwsException
import live.lingting.framework.aws.properties.AwsProperties
import live.lingting.framework.http.HttpResponse
import live.lingting.framework.http.HttpUrlBuilder
import live.lingting.framework.http.QueryBuilder
import live.lingting.framework.http.api.ApiClient
import live.lingting.framework.http.body.Body
import live.lingting.framework.http.body.MemoryBody
import live.lingting.framework.http.header.HttpHeaders
import live.lingting.framework.value.multi.StringMultiValue

/**
 * @author lingting 2025/6/3 15:41
 */
abstract class AwsClient<R : AwsRequest>(properties: AwsProperties) : ApiClient<R>(properties.host(), properties.ssl) {

    protected val ak: String = properties.ak

    protected val sk: String = properties.sk

    protected val token: String? = properties.token

    override fun checkout(
        request: R,
        response: HttpResponse
    ): HttpResponse {
        if (response.isOk) {
            return response
        }

        val string = response.string()
        val headers = response.request().headers()
        log.error(
            "aws api call error! client: {} name: {}; uri: {}; authorization: {}; httpStatus: {}; body:\n{}",
            javaClass.simpleName,
            request.action(),
            response.uri(),
            headers.authorization(),
            response.code(),
            string
        )
        throw AwsException("request error! action: ${request.action()}; code: ${response.code()}")
    }

    fun body(value: StringMultiValue): MemoryBody {
        val query = QueryBuilder(value).also {
            it.sort = true
            it.indexSuffix = true
            it.indexSuffixAll = false
            it.indexMatchNames =
                setOf("PolicyArns.member", "ProvidedContexts.member", "Tags.member", "TransitiveTagKeys.member")
        }.build()
        return MemoryBody(query)
    }

    override fun call(
        urlBuilder: HttpUrlBuilder,
        r: R,
        headers: HttpHeaders,
        body: Body
    ): HttpResponse {
        urlBuilder.addParam("Action", r.action())
        urlBuilder.addParam("Version", r.version())

        val body = body(urlBuilder.params())
        urlBuilder.clearParams()

        val signer = AwsV4Signer(
            r.method(),
            urlBuilder.buildPath(),
            headers,
            body,
            urlBuilder.params(),
            r.region(),
            ak,
            sk,
            service()
        )

        val signed = signer.signed()
        signed.fill(headers)
        return super.call(urlBuilder, r, headers, body)
    }

    abstract fun service(): String

}
