package live.lingting.framework.security

import live.lingting.framework.security.authorize.SecurityAuthorizationService
import live.lingting.framework.security.convert.SecurityConvert
import live.lingting.framework.security.domain.AuthorizationVO
import live.lingting.framework.security.domain.SecurityResultCode.A_EXPIRED
import live.lingting.framework.security.domain.SecurityResultCode.A_LOGIN_ILLEGAL
import live.lingting.framework.security.domain.SecurityResultCode.A_PASSWORD_ILLEGAL
import live.lingting.framework.security.domain.SecurityResultCode.A_TOKEN_INVALID
import live.lingting.framework.security.domain.SecurityToken
import live.lingting.framework.security.password.SecurityPassword
import live.lingting.framework.security.po.EndpointPasswordPO
import live.lingting.framework.security.po.EndpointTokenPO
import live.lingting.framework.security.resolver.SecurityTokenResolverRegistry
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
    val registry: SecurityTokenResolverRegistry,
) {

    open fun logout(): AuthorizationVO {
        val scope = scope()
        store.deleted(scope!!)
        return convert.scopeToVo(scope)
    }

    open fun password(po: EndpointPasswordPO): AuthorizationVO {
        val username = po.username
        val rawPassword = po.password
        val password = try {
            securityPassword.decodeFront(rawPassword!!)
        } catch (_: Throwable) {
            null
        } ?: throw A_PASSWORD_ILLEGAL.toException()
        val scope = service.validAndBuildScope(username, password)
        if (scope == null) {
            throw A_LOGIN_ILLEGAL.toException()
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
            throw A_EXPIRED.toException()
        }
        store.update(scope)
        return convert.scopeToVo(scope)
    }

    open fun resolve(po: EndpointTokenPO): AuthorizationVO {
        val token = token(po)
        val scope = registry.resolver(token)
        if (scope == null || !scope.isLogin || !scope.enabled()) {
            throw A_TOKEN_INVALID.toException()
        }
        return convert.scopeToVo(scope)
    }
}
