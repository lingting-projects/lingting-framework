package live.lingting.framework.aws.s3

import live.lingting.framework.aws.AwsUtils
import live.lingting.framework.aws.s3.impl.S3Meta
import live.lingting.framework.http.header.HttpHeaders

/**
 * @author lingting 2025/1/15 11:03
 */
class AwsS3Meta @JvmOverloads constructor(source: HttpHeaders? = null) : S3Meta(AwsUtils.HEADER_PREFIX_META) {

    init {
        source?.run { from(this) }
    }

}
