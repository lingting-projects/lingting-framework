package live.lingting.framework.ali

import live.lingting.framework.ali.exception.AliException
import live.lingting.framework.ali.properties.AliProperties
import live.lingting.framework.http.HttpResponse
import live.lingting.framework.http.HttpUrlBuilder
import live.lingting.framework.http.api.ApiClient
import live.lingting.framework.http.body.BodySource
import live.lingting.framework.http.header.HttpHeaders
import live.lingting.framework.util.StringUtils

/**
 * @author lingting 2024-09-14 13:49
 */
abstract class AliClient<R : AliRequest> protected constructor(properties: AliProperties) :
    ApiClient<R>(properties.host(), properties.ssl) {

    protected val ak: String = properties.ak

    protected val sk: String = properties.sk

    protected val token: String? = properties.token

    override fun checkout(request: R, response: HttpResponse): HttpResponse {
        if (response.isOk) {
            return response
        }

        val string = response.string()
        val headers = response.request().headers()
        log.error(
            "Ali api call error! client: {} name: {}; uri: {}; authorization: {}; httpStatus: {}; body:\n{}",
            javaClass.simpleName,
            request.name(),
            response.uri(),
            headers.authorization(),
            response.code(),
            string
        )
        throw AliException("request error! name: ${request.name()}; code: ${response.code()}")
    }

    override fun call(
        urlBuilder: HttpUrlBuilder,
        r: R,
        headers: HttpHeaders,
        body: BodySource
    ): HttpResponse {
        val name = r.name()
        val version = r.version()
        val nonce = r.nonce()

        headers.put("${AliV3Signer.HEADER_PREFIX}-action", name)
        headers.put("${AliV3Signer.HEADER_PREFIX}-version", version)
        headers.put("${AliV3Signer.HEADER_PREFIX}-signature-nonce", nonce)

        if (StringUtils.hasText(token)) {
            headers.put("${AliV3Signer.HEADER_PREFIX}-security-token", token!!)
        }

        val signer = AliV3Signer(
            r.method(),
            urlBuilder.buildPath(),
            headers,
            body,
            urlBuilder.params(),
            ak,
            sk
        )

        val signed = signer.signed()
        signed.fill(headers, urlBuilder)

        headers.authorization(signed.authorization)
        return super.call(urlBuilder, r, headers, body)
    }

}
