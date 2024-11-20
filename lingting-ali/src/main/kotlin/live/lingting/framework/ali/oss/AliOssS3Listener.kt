package live.lingting.framework.ali.oss

import java.nio.charset.StandardCharsets
import live.lingting.framework.ali.AliUtils
import live.lingting.framework.ali.exception.AliOssException
import live.lingting.framework.aws.AwsS3Client
import live.lingting.framework.aws.s3.AwsS3Request
import live.lingting.framework.aws.s3.impl.AwsS3DefaultListener
import live.lingting.framework.http.HttpResponse
import live.lingting.framework.util.StringUtils

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
                val base64 = StringUtils.base64(err)
                string = String(base64, StandardCharsets.UTF_8)
            }
        }

        log.error(
            "AliOss call error! uri: {}; code: {}; ec: {}; body:\n{}", response.uri(), response.code(), ec,
            string
        )
        throw AliOssException("request error! code: " + response.code())
    }
}
