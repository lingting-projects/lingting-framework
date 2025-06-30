package live.lingting.framework.huawei.obs

import live.lingting.framework.aws.s3.impl.S3Meta
import live.lingting.framework.http.header.HttpHeaders
import live.lingting.framework.huawei.HuaweiObs

/**
 * @author lingting 2024-09-13 17:08
 */
class HuaweiObsMeta @JvmOverloads constructor(source: HttpHeaders? = null) : S3Meta(HuaweiObs.HEADER_PREFIX_META) {

    init {
        source?.run { from(this) }
    }

    fun multipartUploadId(): String? {
        return first("x-obs-uploadId")
    }

}
