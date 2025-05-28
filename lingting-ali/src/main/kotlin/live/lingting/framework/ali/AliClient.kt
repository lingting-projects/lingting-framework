package live.lingting.framework.ali

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

    override fun call(
        urlBuilder: HttpUrlBuilder,
        r: R,
        headers: HttpHeaders,
        body: BodySource
    ): HttpResponse {
        val name = r.name()
        val version = r.version()
        val nonce = r.nonce()

        headers.put("x-acs-action", name)
        headers.put("x-acs-version", version)
        headers.put("x-acs-signature-nonce", nonce)

        if (StringUtils.hasText(token)) {
            headers.put("x-acs-security-token", token!!)
        }

        val signer = AliV3Signer(
            r.method(),
            urlBuilder.buildPath(),
            headers,
            if (body.length() < 1) null else body,
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
