package live.lingting.framework.security.domain

/**
 * @author lingting 2023-03-30 13:54
 */
open class AuthorizationVO {
    var token: String = ""

    var tenantId: String = ""

    var userId: String = ""

    var username: String = ""

    var avatar: String = ""

    var nickname: String = ""

    /**
     * 是否启用
     */
    var enabled: Boolean = false

    var roles: Set<String> = emptySet()

    var permissions: Set<String> = emptySet()

    var attributes: SecurityScopeAttributes = SecurityScopeAttributes()

    fun from(vo: AuthorizationVO) {
        this.token = vo.token
        this.tenantId = vo.tenantId
        this.userId = vo.userId
        this.username = vo.username
        this.avatar = vo.avatar
        this.nickname = vo.nickname
        this.enabled = vo.enabled
        this.roles = vo.roles.toSet()
        this.permissions = vo.permissions.toSet()
        this.attributes = SecurityScopeAttributes().apply {
            putAll(vo.attributes)
        }
    }
}
