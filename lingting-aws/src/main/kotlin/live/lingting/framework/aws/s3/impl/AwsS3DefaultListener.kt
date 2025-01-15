package live.lingting.framework.aws.s3.impl


import java.time.LocalDateTime
import live.lingting.framework.aws.AwsS3Client
import live.lingting.framework.aws.exception.AwsS3Exception
import live.lingting.framework.aws.s3.AwsS3Request
import live.lingting.framework.aws.s3.AwsS3SingV4
import live.lingting.framework.aws.s3.AwsS3Utils
import live.lingting.framework.aws.s3.interfaces.AwsS3Listener
import live.lingting.framework.http.HttpResponse
import live.lingting.framework.http.header.HttpHeaders
import live.lingting.framework.value.multi.StringMultiValue

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

    override fun onAuthorization(request: AwsS3Request, headers: HttpHeaders, params: StringMultiValue, now: LocalDateTime) {
        val date: String = AwsS3Utils.format(now, AwsS3SingV4.DATETIME_FORMATTER)
        headers.put(AwsS3Utils.HEADER_DATE, date)

        val properties = client.properties
        val sing: AwsS3SingV4 = AwsS3SingV4.builder()
            .dateTime(now)
            .method(request.method())
            .path(request.path())
            .headers(headers)
            .bodySha256(AwsS3Utils.PAYLOAD_UNSIGNED)
            .params(params)
            .region(properties.region)
            .ak(properties.ak)
            .sk(properties.sk)
            .bucket(properties.bucket)
            .build()

        val authorization = sing.calculate()
        headers.authorization(authorization)
    }
}
