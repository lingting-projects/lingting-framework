package live.lingting.framework.aws


import live.lingting.framework.aws.policy.Acl
import live.lingting.framework.aws.s3.AwsS3Request
import live.lingting.framework.aws.s3.AwsS3Utils
import live.lingting.framework.aws.s3.enums.HostStyle
import live.lingting.framework.aws.s3.impl.AwsS3DefaultListener
import live.lingting.framework.aws.s3.interfaces.AwsS3Listener
import live.lingting.framework.aws.s3.properties.S3Properties
import live.lingting.framework.http.HttpResponse
import live.lingting.framework.http.api.ApiClient
import live.lingting.framework.http.body.BodySource
import live.lingting.framework.http.header.HttpHeaders
import live.lingting.framework.time.DateTime
import live.lingting.framework.util.StringUtils
import live.lingting.framework.value.multi.StringMultiValue

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

    override fun customize(request: AwsS3Request) {
        if (properties.hostStyle == HostStyle.SECOND) {
            request.bucket = bucket
        }
    }

    override fun customize(request: AwsS3Request, headers: HttpHeaders, source: BodySource, params: StringMultiValue) {
        if (request.acl != null) {
            headers.put(AwsS3Utils.HEADER_ACL, request.acl!!.value)
        }

        val now = DateTime.current()
        headers.put(AwsS3Utils.HEADER_CONTENT_SHA256, AwsS3Utils.PAYLOAD_UNSIGNED)

        if (StringUtils.hasText(token)) {
            headers.put(AwsS3Utils.HEADER_TOKEN, token!!)
        }

        request.meta.forEach { k, vs ->
            val key = if (k.startsWith(AwsS3Utils.HEADER_PREFIX_META)) k else "${AwsS3Utils.HEADER_PREFIX_META}$k"
            headers.addAll(key, vs)
        }

        listener.onAuthorization(request, headers, params, now)
    }

    override fun checkout(request: AwsS3Request, response: HttpResponse): HttpResponse {
        if (!response.is2xx) {
            listener.onFailed(request, response)
        }
        return response
    }

}
