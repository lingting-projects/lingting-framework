package live.lingting.framework.security.convert

import live.lingting.framework.security.domain.AuthorizationVO
import live.lingting.framework.security.domain.SecurityScope

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
        val rawVO: AuthorizationVO = SecurityMapstruct.INSTANCE.toVo(scope)
        return voExpand(rawVO)
    }

    fun voToScope(vo: AuthorizationVO?): SecurityScope {
        val rawScope: SecurityScope = SecurityMapstruct.INSTANCE.ofVo(vo)
        return scopeExpand(rawScope)
    }
}
