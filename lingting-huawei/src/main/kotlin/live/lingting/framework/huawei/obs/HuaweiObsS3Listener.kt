package live.lingting.framework.huawei.obs

import live.lingting.framework.aws.AwsS3Client
import live.lingting.framework.aws.s3.AwsS3Request
import live.lingting.framework.aws.s3.AwsS3Utils
import live.lingting.framework.aws.s3.impl.AwsS3DefaultListener
import live.lingting.framework.http.HttpResponse
import live.lingting.framework.http.header.HttpHeaders
import live.lingting.framework.huawei.HuaweiObs
import live.lingting.framework.huawei.HuaweiUtils
import live.lingting.framework.huawei.exception.HuaweiObsException
import live.lingting.framework.value.multi.StringMultiValue
import java.time.LocalDateTime
import java.util.function.Consumer

/**
 * @author lingting 2024/11/5 14:54
 */
class HuaweiObsS3Listener(client: AwsS3Client) : AwsS3DefaultListener(client) {
    override fun onFailed(request: AwsS3Request?, response: HttpResponse) {
        val string = response.string()
        log.error("Call error! uri: {}; code: {}; body:\n{}", response.uri(), response.code(), string)
        throw HuaweiObsException("request error! code: " + response.code())
    }

    override fun onAuthorization(request: AwsS3Request, headers: HttpHeaders, params: StringMultiValue?, now: LocalDateTime) {
        val date: String = HuaweiUtils.Companion.format(now)
        headers.put(HuaweiUtils.Companion.HEADER_DATE, date)

        headers.keys().forEach(Consumer<String> { name: String ->
            if (name.startsWith(AwsS3Utils.HEADER_PREFIX)) {
                val newName = name.replace(AwsS3Utils.HEADER_PREFIX, HuaweiObs.Companion.HEADER_PREFIX)
                headers.replace(name, newName)
            }
        })
        val sing: HuaweiObsSing = HuaweiObsSing.Companion.builder()
            .dateTime(now)
            .method(request.method())
            .path(request.path())
            .headers(headers)
            .bodySha256(AwsS3Utils.PAYLOAD_UNSIGNED)
            .params(params)
            .region(region)
            .ak(client.ak)
            .sk(client.sk)
            .bucket(client.bucket)
            .build()

        val authorization = sing.calculate()
        headers.authorization(authorization)
    }
}
