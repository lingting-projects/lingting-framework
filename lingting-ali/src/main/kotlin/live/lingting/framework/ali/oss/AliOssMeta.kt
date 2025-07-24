package live.lingting.framework.ali.oss

import live.lingting.framework.ali.AliUtils
import live.lingting.framework.aws.s3.impl.S3Meta
import live.lingting.framework.value.MultiValue

/**
 * @author lingting 2025/6/4 20:55
 */
class AliOssMeta @JvmOverloads constructor(source: MultiValue<String, String, out Collection<String>>? = null) :
    S3Meta(AliUtils.HEADER_PREFIX_META) {

    init {
        source?.run { from(this) }
    }

}
