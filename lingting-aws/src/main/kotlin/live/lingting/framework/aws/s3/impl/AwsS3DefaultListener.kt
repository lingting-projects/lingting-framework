package live.lingting.framework.aws.s3.impl


import live.lingting.framework.aws.AwsS3Client
import live.lingting.framework.aws.AwsV4Signer
import live.lingting.framework.aws.exception.AwsS3Exception
import live.lingting.framework.aws.s3.AwsS3Request
import live.lingting.framework.aws.s3.interfaces.AwsS3Listener
import live.lingting.framework.http.HttpResponse
import live.lingting.framework.http.HttpUrlBuilder
import live.lingting.framework.http.header.HttpHeaders
import java.time.LocalDateTime

/**
 * @author lingting 2024/11/5 14:48
 */
open class AwsS3DefaultListener(@JvmField protected val client: AwsS3Client) : AwsS3Listener {

    @JvmField
    protected val log = client.log

    override fun onFailed(request: AwsS3Request, response: HttpResponse) {
        val string = response.string()
        log.error("Call error! uri: {}; code: {}; body:\n{}", response.uri(), response.code(), string)
        throw AwsS3Exception("request error! code: " + response.code())
    }

    override fun onAuthorization(request: AwsS3Request, headers: HttpHeaders, url: HttpUrlBuilder, now: LocalDateTime) {
        val properties = client.properties

        val signed = AwsV4Signer(
            request.method(),
            request.path(),
            request.headers,
            null,
            request.params,
            properties.region,
            properties.ak,
            properties.sk,
            "s3"
        ).signed()
        headers.putAll(signed.headers)
        val authorization = signed.authorization
        headers.authorization(authorization)
    }

}
