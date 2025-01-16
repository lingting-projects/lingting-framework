package live.lingting.framework.huawei.iam

import live.lingting.framework.http.body.MemoryBody
import live.lingting.framework.jackson.JacksonUtils

/**
 * @author lingting 2024-09-12 21:38
 */
class HuaweiIamTokenRequest : HuaweiIamRequest() {
    var domain: Map<String, Any>? = null

    var username: String? = null

    var password: String? = null

    override fun usingToken(): Boolean {
        return false
    }

    override fun path(): String {
        return "v3/auth/tokens"
    }

    override fun body(): MemoryBody {
        val map = buildMap {
            put("auth", buildMap {
                put("identity", buildMap {
                    put("methods", VALUE_METHODS)
                    put(KEY_PASSWORD, buildMap {
                        put("user", buildMap {
                            put("domain", domain)
                            put("name", username)
                            put(KEY_PASSWORD, password)
                        })
                    })
                })
            })
        }
        val json = JacksonUtils.toJson(map)
        return MemoryBody(json)
    }

    companion object {
        const val KEY_PASSWORD: String = "password"

        @JvmField
        val VALUE_METHODS: Array<String> = arrayOf(KEY_PASSWORD)
    }
}
