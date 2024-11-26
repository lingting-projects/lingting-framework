package live.lingting.framework.security

import live.lingting.framework.security.authorize.SecurityAuthorizationService
import live.lingting.framework.security.convert.SecurityConvert
import live.lingting.framework.security.domain.AuthorizationVO
import live.lingting.framework.security.domain.SecurityToken
import live.lingting.framework.security.exception.AuthorizationException
import live.lingting.framework.security.password.SecurityPassword
import live.lingting.framework.security.po.EndpointPasswordPO
import live.lingting.framework.security.po.EndpointTokenPO
import live.lingting.framework.security.resource.SecurityHolder.authorization
import live.lingting.framework.security.resource.SecurityHolder.scope
import live.lingting.framework.security.store.SecurityStore

/**
 * @author lingting 2024/11/26 20:27
 */
open class SecurityEndpointService(
    val service: SecurityAuthorizationService,
    val store: SecurityStore,
    val securityPassword: SecurityPassword,
    val convert: SecurityConvert,
) {

    open fun logout(): AuthorizationVO {
        val scope = scope()
        store.deleted(scope!!)
        return convert.scopeToVo(scope)
    }

    open fun password(po: EndpointPasswordPO): AuthorizationVO {
        val username = po.username
        val rawPassword = po.password
        val password = securityPassword.decodeFront(rawPassword!!)
        val scope = service.validAndBuildScope(username, password)
        if (scope == null) {
            throw AuthorizationException("Username or password is incorrect!")
        }
        store.save(scope)
        return convert.scopeToVo(scope)
    }

    open fun token(po: EndpointTokenPO): SecurityToken {
        return if (po.value.isNullOrBlank()) {
            val authorization = authorization()
            convert.toToken(authorization)
        } else {
            convert.toToken(po)
        }
    }

    open fun refresh(po: EndpointTokenPO): AuthorizationVO {
        val token = token(po)
        val scope = service.refresh(token)
        if (scope == null) {
            throw AuthorizationException("Login authorization has expired!")
        }
        store.update(scope)
        return convert.scopeToVo(scope)
    }

    open fun resolve(po: EndpointTokenPO): AuthorizationVO {
        val token = token(po)
        val scope = store.get(token)
        if (scope == null || !scope.isLogin || !scope.enabled()) {
            throw AuthorizationException("Token is invalid!")
        }
        return convert.scopeToVo(scope)
    }
}
