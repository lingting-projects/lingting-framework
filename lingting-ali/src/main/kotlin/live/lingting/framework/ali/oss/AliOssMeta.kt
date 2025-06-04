package live.lingting.framework.ali.oss

import live.lingting.framework.ali.AliUtils
import live.lingting.framework.aws.s3.AwsS3Meta
import live.lingting.framework.http.header.HttpHeaders

/**
 * @author lingting 2025/6/4 20:55
 */
class AliOssMeta @JvmOverloads constructor(value: HttpHeaders? = null) : AwsS3Meta(AliUtils.HEADER_PREFIX_META, value) {
}
