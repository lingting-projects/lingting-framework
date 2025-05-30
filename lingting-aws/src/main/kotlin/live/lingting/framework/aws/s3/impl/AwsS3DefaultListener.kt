package live.lingting.framework.aws.s3.impl


import live.lingting.framework.aws.AwsS3Client
import live.lingting.framework.aws.AwsSigner
import live.lingting.framework.aws.AwsV4Signer
import live.lingting.framework.aws.exception.AwsS3Exception
import live.lingting.framework.aws.s3.AwsS3Request
import live.lingting.framework.aws.s3.interfaces.AwsS3Listener
import live.lingting.framework.http.HttpResponse
import live.lingting.framework.http.HttpUrlBuilder
import live.lingting.framework.http.header.HttpHeaders

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

    override fun onSign(
        request: AwsS3Request,
        headers: HttpHeaders,
        url: HttpUrlBuilder
    ): AwsSigner<*, *> {
        val properties = client.properties

        return AwsV4Signer(
            request.method(),
            url.buildPath(),
            headers,
            null,
            url.params(),
            properties.region,
            properties.ak,
            properties.sk,
            "s3"
        )
    }

}
