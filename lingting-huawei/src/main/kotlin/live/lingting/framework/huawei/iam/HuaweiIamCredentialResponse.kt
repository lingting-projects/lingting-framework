package live.lingting.framework.huawei.iam

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author lingting 2024-09-13 14:00
 */
class HuaweiIamCredentialResponse {
    var credential: Credential? = null

    val access: String?
        get() = credential!!.access

    val secret: String?
        get() = credential!!.secret

    val securityToken: String?
        get() = credential!!.securityToken

    val expire: String?
        get() = credential!!.expire

    class Credential {
        var access: String? = null

        var secret: String? = null

        @set:JsonProperty("securitytoken")
        @JsonProperty("securitytoken")
        var securityToken: String? = null

        @set:JsonProperty("expires_at")
        @JsonProperty("expires_at")
        var expire: String? = null
    }
}
