package live.lingting.framework.huawei.iam

import live.lingting.framework.http.body.BodySource
import live.lingting.framework.http.body.MemoryBody
import live.lingting.framework.huawei.HuaweiStatement
import live.lingting.framework.jackson.JacksonUtils
import java.time.Duration

/**
 * @author lingting 2024-09-13 13:53
 */
class HuaweiIamCredentialRequest : HuaweiIamRequest() {
    var timeout: Duration? = null

    var statements: Collection<HuaweiStatement>? = null

    override fun path(): String {
        return "v3.0/OS-CREDENTIAL/securitytokens"
    }

    override fun body(): BodySource? {
        val policy: MutableMap<String, Any> = HashMap()
        policy["Version"] = "1.1"
        policy["Statement"] = statements!!.stream().map<Map<String?, Any?>?> { obj: HuaweiStatement -> obj.map() }.toList()

        val token = java.util.Map.of<String, Any>("duration_seconds", timeout!!.seconds)

        val identity = java.util.Map.of("methods", VALUE_METHODS, "token", token, "policy", policy)
        val auth = java.util.Map.of<String, Any>("identity", identity)
        val params = java.util.Map.of<String, Any>("auth", auth)
        val json = JacksonUtils.toJson(params)
        return MemoryBody(json)
    }

    companion object {
        protected val VALUE_METHODS: Array<String> = arrayOf("token")
    }
}
