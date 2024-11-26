package live.lingting.framework.security.convert

import live.lingting.framework.security.domain.AuthorizationVO
import live.lingting.framework.security.domain.SecurityScope
import live.lingting.framework.security.domain.SecurityScopeAttributes
import live.lingting.framework.security.domain.SecurityToken
import live.lingting.framework.security.po.EndpointTokenPO

/**
 * @author lingting 2024-01-30 19:24
 */
interface SecurityConvert {

    fun scopeExpand(scope: SecurityScope): SecurityScope {
        return scope
    }

    fun voExpand(vo: AuthorizationVO): AuthorizationVO {
        return vo
    }

    fun scopeToVo(scope: SecurityScope?): AuthorizationVO {
        val vo = AuthorizationVO()
        if (scope != null) {
            vo.authorization = scope.authorization
            vo.tenantId = scope.tenantId ?: ""
            vo.userId = scope.userId ?: ""
            vo.username = scope.username
            vo.avatar = scope.avatar
            vo.nickname = scope.nickname
            vo.enabled = scope.enabled
            vo.roles = scope.roles.toSet()
            vo.permissions = scope.permissions.toSet()
            vo.attributes = SecurityScopeAttributes().apply {
                putAll(scope.attributes)
            }
        }
        return voExpand(vo)
    }

    fun voToScope(vo: AuthorizationVO?): SecurityScope {
        val scope = SecurityScope()
        if (vo != null) {
            scope.authorization = vo.authorization
            scope.tenantId = vo.tenantId
            scope.userId = vo.userId
            scope.username = vo.username
            scope.password = ""
            scope.avatar = vo.avatar
            scope.nickname = vo.nickname
            scope.enabled = vo.enabled
            scope.expireTime = Long.MAX_VALUE
            scope.roles = vo.roles.toSet()
            scope.permissions = vo.permissions.toSet()
            scope.attributes = SecurityScopeAttributes().apply {
                putAll(vo.attributes)
            }
        }
        return scopeExpand(scope)
    }

    fun toToken(raw: String): SecurityToken {
        return SecurityToken.ofDelimiter(raw)
    }

    fun toToken(po: EndpointTokenPO): SecurityToken {
        val raw = po.raw
        val value = po.value
        if (!value.isNullOrEmpty()) {
            return SecurityToken.of(po.type ?: "", value, raw ?: "")
        }
        if (!raw.isNullOrBlank()) {
            return toToken(raw)
        }
        return SecurityToken.EMPTY
    }

}
