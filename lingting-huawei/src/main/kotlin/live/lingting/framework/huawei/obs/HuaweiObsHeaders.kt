package live.lingting.framework.huawei.obs

import live.lingting.framework.aws.s3.AwsS3Meta
import live.lingting.framework.http.header.HttpHeaders
import live.lingting.framework.huawei.HuaweiObs

/**
 * @author lingting 2024-09-13 17:08
 */
class HuaweiObsHeaders(value: HttpHeaders) : AwsS3Meta(HuaweiObs.HEADER_PREFIX_META, value) {

    fun multipartUploadId(): String {
        return first("x-obs-uploadId")!!
    }

}
