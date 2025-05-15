package live.lingting.framework.aws.s3.impl


import live.lingting.framework.aws.AwsS3Client
import live.lingting.framework.aws.AwsSignV4
import live.lingting.framework.aws.AwsUtils
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
        val date: String = AwsUtils.format(now, AwsSignV4.DATETIME_FORMATTER)
        headers.put(AwsUtils.HEADER_DATE, date)

        val properties = client.properties
        val sing: AwsSignV4 = AwsSignV4.builder()
            .dateTime(now)
            .method(request.method())
            .path(url.buildPath())
            .headers(headers)
            .bodySha256(AwsUtils.PAYLOAD_UNSIGNED)
            .params(url.params())
            .region(properties.region)
            .ak(properties.ak)
            .sk(properties.sk)
            .bucket(properties.bucket)
            .service("s3")
            .build()

        val authorization = sing.calculate()
        headers.authorization(authorization)
    }

}
