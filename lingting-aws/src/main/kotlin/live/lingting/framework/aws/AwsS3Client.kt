package live.lingting.framework.aws


import live.lingting.framework.aws.policy.Acl
import live.lingting.framework.aws.properties.S3Properties
import live.lingting.framework.aws.s3.AwsS3PreRequest
import live.lingting.framework.aws.s3.AwsS3Request
import live.lingting.framework.aws.s3.impl.AwsS3DefaultListener
import live.lingting.framework.aws.s3.interfaces.AwsS3Listener
import live.lingting.framework.aws.s3.response.AwsS3PreSignedResponse
import live.lingting.framework.http.HttpResponse
import live.lingting.framework.http.HttpUrlBuilder
import live.lingting.framework.http.api.ApiClient
import live.lingting.framework.http.body.Body
import live.lingting.framework.http.header.HttpHeaders
import live.lingting.framework.jackson.JacksonUtils
import live.lingting.framework.time.DateTime
import java.io.ByteArrayInputStream
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

/**
 * @author lingting 2024-09-19 15:02
 */
abstract class AwsS3Client protected constructor(val properties: S3Properties) :
    ApiClient<AwsS3Request>(properties.host(), properties.ssl) {

    open val charset: Charset = StandardCharsets.UTF_8

    val ak: String = properties.ak

    val sk: String = properties.sk

    val token: String? = properties.token

    val acl: Acl? = properties.acl

    val bucket: String = properties.bucket

    var listener: AwsS3Listener = AwsS3DefaultListener(this)

    override fun urlBuilder(): HttpUrlBuilder {
        return properties.urlBuilder()
    }

    override fun checkout(request: AwsS3Request, response: HttpResponse): HttpResponse {
        if (!response.is2xx) {
            listener.onFailed(request, response)
        }
        return response
    }

    override fun call(
        urlBuilder: HttpUrlBuilder,
        r: AwsS3Request,
        headers: HttpHeaders,
        body: Body
    ): HttpResponse {
        if (r.acl != null) {
            headers.put(AwsUtils.HEADER_ACL, r.acl!!.value)
        }

        if (!token.isNullOrBlank()) {
            headers.put(AwsUtils.HEADER_TOKEN, token)
        }

        r.meta.forEach { k, vs ->
            val key = if (k.startsWith(AwsUtils.HEADER_PREFIX_META)) k else "${AwsUtils.HEADER_PREFIX_META}$k"
            headers.addAll(key, vs)
        }

        val signer = listener.onSign(r, headers, urlBuilder)

        val current = DateTime.current()

        val signed =
            if (r is AwsS3PreRequest) signer.signed(current, r.expire)
            else signer.signed(current)

        signed.fill(headers, urlBuilder)
        if (r !is AwsS3PreRequest) {
            val request = buildRequest(urlBuilder, headers, r, body)
            return call(r, request)
        }

        val url = urlBuilder.build()
        val value = AwsS3PreSignedResponse(url, signed.headers.map().mapValues { (_, v) -> v.toList() })
        val json = JacksonUtils.toJson(value)
        val bytes = json.toByteArray(charset)

        val request = buildRequest(urlBuilder, HttpHeaders.empty(), r, body)
        return HttpResponse(request, 200, request.headers(), ByteArrayInputStream(bytes))
    }

    open fun pre(request: AwsS3PreRequest): AwsS3PreSignedResponse {
        val expire = request.expire
        checkNotNull(expire) { "pre sign expire must be not null!" }
        check(expire.isPositive) { "pre sign expire must be not positive!" }
        val response = call(request)
        val json = response.string()
        return JacksonUtils.toObj(json, AwsS3PreSignedResponse::class)
    }

}
