package live.lingting.framework.huawei.iam

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author lingting 2024-09-13 11:52
 */
class HuaweiIamTokenResponse {
    var token: Token = Token()

    val expire: String
        get() = token.expire

    val issued: String
        get() = token.issued

    class Token {
        @JsonProperty("expires_at")
        var expire: String = ""

        @JsonProperty("issued_at")
        var issued: String = ""
    }
}
