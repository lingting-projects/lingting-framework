package live.lingting.framework.security.domain

import live.lingting.framework.util.StringUtils

import java.util.function.Function
import java.util.function.Predicate

/**
 * @author lingting 2023-03-29 20:25
 */
open class SecurityScope {
    @JvmField
    var token: String? = null

    @JvmField
    var userId: String? = null

    @JvmField
    var tenantId: String? = null

    @JvmField
    var username: String? = null

    @JvmField
    var password: String? = null

    @JvmField
    var avatar: String? = null

    @JvmField
    var nickname: String? = null

    /**
     * 是否启用
     */
    @JvmField
    var enabled: Boolean? = null

    /**
     * 过期时间的时间戳
     */
    @JvmField
    var expireTime: Long? = null

    @JvmField
    var roles: Set<String>? = null

    @JvmField
    var permissions: Set<String>? = null

    @JvmField
    var attributes: SecurityScopeAttributes? = null

    fun enabled(): Boolean {
        return java.lang.Boolean.TRUE == enabled
    }

    val isLogin: Boolean
        /**
         * 此scope是否为已登录用户
         */
        get() {
            val tokenAvailable = StringUtils.hasText(token)
            val userAvailable = StringUtils.hasText(userId)
            val enableAvailable = enabled != null
            return tokenAvailable && userAvailable && enableAvailable
        }

    fun attribute(key: String): Any? {
        return if (attributes == null) null else attributes!!.find(key)
    }

    fun <T> attribute(key: String, func: Function<Optional<Any?>?, T>): T {
        return func.apply(Optional.ofNullable(attribute(key)))
    }

    fun <T> attribute(key: String, defaultValue: T, func: Function<Any?, T>): T {
        return attribute(key, defaultValue, { obj: Optional<Any?> -> obj.isEmpty }, func)
    }

    /**
     * @param usingDefault 如果返回true表示使用默认值
     */
    fun <T> attribute(
        key: String, defaultValue: T, usingDefault: Predicate<Optional<Any?>>,
        func: Function<Any?, T>
    ): T {
        val optional: Optional<Any?> = Optional.ofNullable(attribute(key))
        if (usingDefault.test(optional)) {
            return defaultValue
        }
        return func.apply(optional)
    }
}
