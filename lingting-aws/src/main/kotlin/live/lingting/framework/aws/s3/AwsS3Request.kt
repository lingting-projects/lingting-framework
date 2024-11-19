package live.lingting.framework.aws.s3

import live.lingting.framework.aws.policy.Acl
import live.lingting.framework.http.api.ApiRequest

/**
 * @author lingting 2024-09-19 15:03
 */
abstract class AwsS3Request : ApiRequest() {
    var key: String? = null

    var acl: Acl? = null

    override fun path(): String {
        return key!!
    }

    fun setAclIfAbsent(acl: Acl) {
        if (this.acl == null) {
            this.acl = acl
        }
    }
}
