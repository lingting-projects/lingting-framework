package live.lingting.framework.huawei.obs

import live.lingting.framework.http.header.HttpHeaders
import live.lingting.framework.http.header.UnmodifiableHttpHeaders

/**
 * @author lingting 2024-09-13 17:08
 */
class HuaweiObsHeaders(value: HttpHeaders) : UnmodifiableHttpHeaders(value.unmodifiable()) {
    fun multipartUploadId(): String? {
        return first("x-obs-uploadId")
    }
}
