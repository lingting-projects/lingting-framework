package live.lingting.framework.security.domain

import java.util.Optional
import java.util.function.Function
import java.util.function.Predicate
import live.lingting.framework.util.OptionalUtils.isEmpty
import live.lingting.framework.util.StringUtils

/**
 * @author lingting 2023-03-29 20:25
 */
open class SecurityScope {
    /**
     * <p>Bearer token</p>
     */
    var authorization: String = ""

    var tenantId: String? = null

    var userId: String? = null

    var username: String = ""

    var password: String = ""

    var avatar: String = ""

    var nickname: String = ""

    /**
     * 是否启用
     */
    var enabled: Boolean = false

    /**
     * 过期时间的时间戳
     */
    var expireTime: Long = 0

    var roles: Set<String> = emptySet()

    var permissions: Set<String> = emptySet()

    var attributes: SecurityScopeAttributes = SecurityScopeAttributes()

    fun enabled(): Boolean {
        return enabled == true
    }

    val isLogin: Boolean
        /**
         * 此scope是否为已登录用户
         */
        get() {
            val tokenAvailable = StringUtils.hasText(authorization)
            val userAvailable = StringUtils.hasText(userId)
            return tokenAvailable && userAvailable
        }

    fun attribute(key: String): Any? {
        return attributes.find(key)
    }

    fun <T> attribute(key: String, func: Function<Optional<Any>, T>): T {
        val any = attribute(key)
        val optional = Optional.ofNullable(any)
        return func.apply(optional)
    }

    fun <T> attribute(key: String, defaultValue: T, func: Function<Any, T>): T {
        return attribute(key, defaultValue, { obj -> obj.isEmpty }, func)
    }

    /**
     * @param usingDefault 如果返回true表示使用默认值
     */
    fun <T> attribute(
        key: String, defaultValue: T, usingDefault: Predicate<Optional<Any>>,
        func: Function<Any, T>
    ): T {
        val optional: Optional<Any> = Optional.ofNullable(attribute(key))
        if (usingDefault.test(optional)) {
            return defaultValue
        }
        return func.apply(optional)
    }

    fun from(scope: SecurityScope) {
        this.authorization = scope.authorization
        this.tenantId = scope.tenantId
        this.userId = scope.userId
        this.username = scope.username
        this.password = scope.password
        this.avatar = scope.avatar
        this.nickname = scope.nickname
        this.enabled = scope.enabled
        this.expireTime = scope.expireTime
        this.roles = scope.roles.toSet()
        this.permissions = scope.permissions.toSet()
        this.attributes = SecurityScopeAttributes().apply {
            putAll(scope.attributes)
        }
    }
}
