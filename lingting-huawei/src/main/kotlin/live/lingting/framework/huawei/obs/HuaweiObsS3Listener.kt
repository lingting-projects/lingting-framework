package live.lingting.framework.huawei.obs


import live.lingting.framework.aws.AwsS3Client
import live.lingting.framework.aws.AwsSigner
import live.lingting.framework.aws.AwsUtils
import live.lingting.framework.aws.s3.AwsS3Request
import live.lingting.framework.aws.s3.enums.HostStyle
import live.lingting.framework.aws.s3.impl.AwsS3DefaultListener
import live.lingting.framework.http.HttpResponse
import live.lingting.framework.http.HttpUrlBuilder
import live.lingting.framework.http.header.HttpHeaders
import live.lingting.framework.huawei.HuaweiObs
import live.lingting.framework.huawei.exception.HuaweiObsException
import java.util.function.Consumer

/**
 * @author lingting 2024/11/5 14:54
 */
class HuaweiObsS3Listener(client: AwsS3Client) : AwsS3DefaultListener(client) {
    override fun onFailed(request: AwsS3Request, response: HttpResponse) {
        val string = response.string()
        log.error("Call error! uri: {}; code: {}; body:\n{}", response.uri(), response.code(), string)
        throw HuaweiObsException("request error! code: " + response.code())
    }

    override fun onSign(
        request: AwsS3Request,
        headers: HttpHeaders,
        url: HttpUrlBuilder
    ): AwsSigner<*, *> {
        val properties = client.properties

        val path = if (properties.hostStyle != HostStyle.VIRTUAL) url.buildPath()
        else url.buildPath().let { s ->
            HttpUrlBuilder().path(properties.bucket).pathSegment(s).buildPath()
                .let {
                    if (s == "/" && !it.endsWith(s)) "$it/" else it
                }
        }

        headers.keys().forEach(Consumer { name ->
            if (name.startsWith(AwsUtils.HEADER_PREFIX)) {
                val newName = name.replace(AwsUtils.HEADER_PREFIX, HuaweiObs.HEADER_PREFIX)
                headers.replace(name, newName)
            }
        })
        return HuaweiObsSigner(
            request.method(),
            path,
            headers,
            null,
            url.params(),
            properties.ak,
            properties.sk
        )
    }

}
