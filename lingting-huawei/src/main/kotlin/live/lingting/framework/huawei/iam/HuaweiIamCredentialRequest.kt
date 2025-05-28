package live.lingting.framework.huawei.iam

import live.lingting.framework.http.body.Body
import live.lingting.framework.http.body.MemoryBody
import live.lingting.framework.huawei.HuaweiStatement
import live.lingting.framework.huawei.HuaweiUtils
import live.lingting.framework.jackson.JacksonUtils
import java.time.Duration

/**
 * @author lingting 2024-09-13 13:53
 */
class HuaweiIamCredentialRequest : HuaweiIamRequest() {
    var timeout: Duration = HuaweiUtils.CREDENTIAL_EXPIRE

    var statements: Collection<HuaweiStatement> = emptyList()

    override fun path(): String {
        return "v3.0/OS-CREDENTIAL/securitytokens"
    }

    override fun body(): Body {
        val map = buildMap {
            put("auth", buildMap {
                put("identity", buildMap {
                    put("methods", VALUE_METHODS)
                    put("token", buildMap {
                        put("duration_seconds", timeout.seconds)
                    })
                    put("policy", buildMap {
                        put("Version", "1.1")
                        put("Statement", statements.map { it.map() })
                    })
                })
            })
        }
        val json = JacksonUtils.toJson(map)
        return MemoryBody(json)
    }

    companion object {
        @JvmField
        val VALUE_METHODS: Array<String> = arrayOf("token")
    }
}
