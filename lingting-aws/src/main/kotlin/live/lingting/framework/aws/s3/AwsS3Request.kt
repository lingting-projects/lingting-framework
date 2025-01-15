package live.lingting.framework.aws.s3

import live.lingting.framework.aws.policy.Acl
import live.lingting.framework.http.api.ApiRequest
import live.lingting.framework.http.header.HttpHeaders

/**
 * @author lingting 2024-09-19 15:03
 */
abstract class AwsS3Request : ApiRequest() {

    var bucket: String = ""

    var key: String = ""

    var acl: Acl? = null

    /**
     * 自定义元数据key(不要前缀, 自动拼)
     */
    val meta: HttpHeaders = HttpHeaders.empty()

    override fun path(): String {
        if (bucket.isBlank()) {
            return key
        }
        if (key.isBlank()) {
            return bucket
        }
        return "$bucket/$key"
    }

    fun setAclIfAbsent(acl: Acl) {
        if (this.acl == null) {
            this.acl = acl
        }
    }
}
