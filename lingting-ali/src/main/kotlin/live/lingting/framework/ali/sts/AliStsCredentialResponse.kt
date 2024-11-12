package live.lingting.framework.ali.sts

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author lingting 2024-09-14 13:50
 */
class AliStsCredentialResponse : AliStsResponse() {
    @set:JsonProperty("Credentials")
    @JsonProperty("Credentials")
    var credentials: Credentials? = null

    val accessKeyId: String?
        get() = credentials!!.accessKeyId

    val accessKeySecret: String?
        get() = credentials!!.accessKeySecret

    val securityToken: String?
        get() = credentials!!.securityToken

    val expire: String?
        get() = credentials!!.expiration

    class Credentials {
        @set:JsonProperty("AccessKeyId")
        @JsonProperty("AccessKeyId")
        var accessKeyId: String? = null

        @set:JsonProperty("AccessKeySecret")
        @JsonProperty("AccessKeySecret")
        var accessKeySecret: String? = null

        @set:JsonProperty("SecurityToken")
        @JsonProperty("SecurityToken")
        var securityToken: String? = null

        @set:JsonProperty("Expiration")
        @JsonProperty("Expiration")
        var expiration: String? = null
    }
}
