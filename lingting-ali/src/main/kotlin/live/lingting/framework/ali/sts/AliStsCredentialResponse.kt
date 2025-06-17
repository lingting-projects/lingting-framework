package live.lingting.framework.ali.sts

import com.fasterxml.jackson.annotation.JsonProperty
import live.lingting.framework.ali.AliResponse

/**
 * @author lingting 2024-09-14 13:50
 */
class AliStsCredentialResponse : AliResponse() {

    @JsonProperty("Credentials")
    var credentials: Credentials = Credentials()

    val accessKeyId: String
        get() = credentials.accessKeyId

    val accessKeySecret: String
        get() = credentials.accessKeySecret

    val securityToken: String
        get() = credentials.securityToken

    val expire: String
        get() = credentials.expiration

    class Credentials {

        @JsonProperty("AccessKeyId")
        var accessKeyId: String = ""

        @JsonProperty("AccessKeySecret")
        var accessKeySecret: String = ""

        @JsonProperty("SecurityToken")
        var securityToken: String = ""

        @JsonProperty("Expiration")
        var expiration: String = ""

    }
}
