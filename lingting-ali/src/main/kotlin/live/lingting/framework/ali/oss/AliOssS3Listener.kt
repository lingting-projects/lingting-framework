package live.lingting.framework.ali.oss

import live.lingting.framework.ali.AliUtils
import live.lingting.framework.ali.AliV4Signer
import live.lingting.framework.ali.exception.AliOssException
import live.lingting.framework.aws.AwsS3Client
import live.lingting.framework.aws.AwsSigner
import live.lingting.framework.aws.AwsUtils
import live.lingting.framework.aws.s3.AwsS3Request
import live.lingting.framework.aws.s3.enums.HostStyle
import live.lingting.framework.aws.s3.impl.AwsS3DefaultListener
import live.lingting.framework.http.HttpResponse
import live.lingting.framework.http.HttpUrlBuilder
import live.lingting.framework.http.header.HttpHeaders
import live.lingting.framework.util.StringUtils
import live.lingting.framework.util.StringUtils.base64
import java.nio.charset.StandardCharsets
import java.util.function.Consumer

/**
 * @author lingting 2024/11/5 14:53
 */
class AliOssS3Listener(client: AwsS3Client) : AwsS3DefaultListener(client) {

    override fun onFailed(request: AwsS3Request, response: HttpResponse) {
        val headers = response.headers()
        val ec = headers.first(AliUtils.HEADER_EC, "")

        var string = response.string()

        if (!StringUtils.hasText(string)) {
            val err = headers.first(AliUtils.HEADER_ERR, "")
            if (StringUtils.hasText(err)) {
                val base64 = err.base64()
                string = String(base64, StandardCharsets.UTF_8)
            }
        }

        log.error(
            "AliOss call error! uri: {}; code: {}; ec: {}; body:\n{}", response.uri(), response.code(), ec,
            string
        )
        throw AliOssException("request error! code: " + response.code())
    }

    override fun onSign(
        request: AwsS3Request,
        headers: HttpHeaders,
        url: HttpUrlBuilder
    ): AwsSigner<*, *> {
        val properties = client.properties

        headers.keys().forEach(Consumer { name ->
            if (name == AwsUtils.HEADER_ACL) {
                headers.replace(name, AliUtils.HEADER_ACL)
            } else if (name.startsWith(AwsUtils.HEADER_PREFIX)) {
                val newName = name.replace(AwsUtils.HEADER_PREFIX, AliUtils.HEADER_PREFIX)
                headers.replace(name, newName)
            }
        })

        val path = if (properties.hostStyle != HostStyle.VIRTUAL) url.buildPath()
        else url.buildPath().let { s ->
            HttpUrlBuilder().path(properties.bucket).pathSegment(s).buildPath()
                .let {
                    if (s == "/" && !it.endsWith(s)) "$it/" else it
                }
        }

        return AliV4Signer(
            request.method(),
            path,
            headers,
            null,
            url.params(),
            properties.region,
            properties.ak,
            properties.sk,
            "oss"
        )
    }

}
