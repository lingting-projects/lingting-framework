package live.lingting.framework.huawei.iam

import live.lingting.framework.http.body.MemoryBody
import live.lingting.framework.jackson.JacksonUtils

/**
 * @author lingting 2024-09-12 21:38
 */
class HuaweiIamTokenRequest : HuaweiIamRequest() {
    var domain: Map<String?, Any?>? = null

    var username: String? = null

    var password: String? = null

    override fun usingToken(): Boolean {
        return false
    }

    override fun path(): String {
        return "v3/auth/tokens"
    }

    override fun body(): MemoryBody? {
        val map: MutableMap<String, Any?> = HashMap()
        map["domain"] = domain
        map["name"] = username
        map[KEY_PASSWORD] = password

        val pwd = java.util.Map.of<String, Any>("user", map)
        val identity = java.util.Map.of("methods", VALUE_METHODS, KEY_PASSWORD, pwd)
        val auth = java.util.Map.of<String, Any>("identity", identity)
        val params = java.util.Map.of<String, Any>("auth", auth)
        val json = JacksonUtils.toJson(params)
        return MemoryBody(json)
    }

    companion object {
        const val KEY_PASSWORD: String = "password"

        protected val VALUE_METHODS: Array<String> = arrayOf(KEY_PASSWORD)
    }
}
