package live.lingting.framework.aws


import live.lingting.framework.aws.policy.Acl
import live.lingting.framework.aws.s3.AwsS3Request
import live.lingting.framework.aws.s3.impl.AwsS3DefaultListener
import live.lingting.framework.aws.s3.interfaces.AwsS3Listener
import live.lingting.framework.aws.s3.properties.S3Properties
import live.lingting.framework.http.HttpResponse
import live.lingting.framework.http.HttpUrlBuilder
import live.lingting.framework.http.api.ApiClient
import live.lingting.framework.http.body.BodySource
import live.lingting.framework.http.header.HttpHeaders
import live.lingting.framework.time.DateTime

/**
 * @author lingting 2024-09-19 15:02
 */
abstract class AwsS3Client protected constructor(val properties: S3Properties) :
    ApiClient<AwsS3Request>(properties.host(), properties.ssl) {

    val ak: String = properties.ak

    val sk: String = properties.sk

    val token: String? = properties.token

    val acl: Acl = properties.acl

    val bucket: String = properties.bucket

    var listener: AwsS3Listener = AwsS3DefaultListener(this)

    override fun urlBuilder(): HttpUrlBuilder {
        return properties.urlBuilder()
    }

    override fun customize(request: AwsS3Request, headers: HttpHeaders, source: BodySource, url: HttpUrlBuilder) {
        if (request.acl != null) {
            headers.put(AwsUtils.HEADER_ACL, request.acl!!.value)
        }

        val now = DateTime.current()

        if (!token.isNullOrBlank()) {
            headers.put(AwsUtils.HEADER_TOKEN, token)
        }

        request.meta.forEach { k, vs ->
            val key = if (k.startsWith(AwsUtils.HEADER_PREFIX_META)) k else "${AwsUtils.HEADER_PREFIX_META}$k"
            headers.addAll(key, vs)
        }

        listener.onAuthorization(request, headers, url, now)
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
        body: BodySource
    ): HttpResponse {

        return super.call(urlBuilder, r, headers, body)
    }

}
