package live.lingting.framework.aws

import java.time.LocalDateTime
import live.lingting.framework.aws.policy.Acl
import live.lingting.framework.aws.s3.AwsS3Properties
import live.lingting.framework.aws.s3.AwsS3Request
import live.lingting.framework.aws.s3.AwsS3Utils
import live.lingting.framework.aws.s3.impl.AwsS3DefaultListener
import live.lingting.framework.aws.s3.interfaces.AwsS3Listener
import live.lingting.framework.http.HttpResponse
import live.lingting.framework.http.api.ApiClient
import live.lingting.framework.http.body.BodySource
import live.lingting.framework.http.header.HttpHeaders
import live.lingting.framework.util.StringUtils
import live.lingting.framework.value.multi.StringMultiValue

/**
 * @author lingting 2024-09-19 15:02
 */
abstract class AwsS3Client protected constructor(val properties: AwsS3Properties) : ApiClient<AwsS3Request?>(properties.host()) {
    @JvmField
    val ak: String? = properties.ak

    @JvmField
    val sk: String? = properties.sk

    val token: String? = properties.token

    val acl: Acl? = properties.acl

    @JvmField
    val bucket: String? = properties.bucket

    @JvmField
    var listener: AwsS3Listener = AwsS3DefaultListener(this)

    override fun customize(request: AwsS3Request, headers: HttpHeaders, source: BodySource?, params: StringMultiValue?) {
        if (request.acl != null) {
            headers.put(AwsS3Utils.HEADER_ACL, request.acl.value)
        }

        val now = LocalDateTime.now()
        headers.put(AwsS3Utils.HEADER_CONTENT_SHA256, AwsS3Utils.PAYLOAD_UNSIGNED)

        if (StringUtils.hasText(token)) {
            headers.put(AwsS3Utils.HEADER_TOKEN, token)
        }
        listener.onAuthorization(request, headers, params, now)
    }

    override fun checkout(request: AwsS3Request, response: HttpResponse): HttpResponse {
        if (!response.is2xx()) {
            listener.onFailed(request, response)
        }
        return response
    }
}
