package live.lingting.framework.security.domain

/**
 * @author lingting 2023-03-30 13:54
 */
open class AuthorizationVO {
    @JvmField
    var token: String? = null

    @JvmField
    var tenantId: String? = null

    @JvmField
    var userId: String? = null

    @JvmField
    var username: String? = null

    @JvmField
    var avatar: String? = null

    @JvmField
    var nickname: String? = null

    /**
     * 是否启用
     */
    @JvmField
    var enabled: Boolean? = null

    @JvmField
    var roles: Set<String>? = null

    @JvmField
    var permissions: Set<String>? = null

    @JvmField
    var attributes: SecurityScopeAttributes? = null
}
