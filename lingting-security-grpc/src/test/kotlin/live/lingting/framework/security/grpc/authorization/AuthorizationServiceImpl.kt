package live.lingting.framework.security.grpc.authorization


import live.lingting.framework.security.authorize.SecurityAuthorizationService
import live.lingting.framework.security.domain.SecurityScope
import live.lingting.framework.security.domain.SecurityScopeAttributes
import live.lingting.framework.security.domain.SecurityToken
import live.lingting.framework.security.exception.AuthorizationException
import live.lingting.framework.security.store.SecurityStore
import live.lingting.framework.time.DateTime
import live.lingting.framework.util.LocalDateTimeUtils.timestamp
import live.lingting.framework.util.MdcUtils

/**
 * @author lingting 2024-01-30 20:30
 */
class AuthorizationServiceImpl(private val store: SecurityStore) : SecurityAuthorizationService {

    override fun validAndBuildScope(username: String?, password: String?): SecurityScope? {
        if (username != "user" && username != "admin") {
            throw AuthorizationException()
        }
        val scope = SecurityScope()
        scope.authorization = username
        scope.tenantId = username
        scope.userId = username
        scope.username = username
        scope.password = password!!
        scope.avatar = ""
        scope.nickname = username
        scope.enabled = true
        scope.expireTime = expireTime()
        scope.roles = setOf(username)
        scope.permissions = setOf(username)
        val attributes = SecurityScopeAttributes()
        attributes["expand"] = "true"
        attributes["tag"] = MdcUtils.traceId()
        scope.attributes = attributes
        return scope
    }

    fun expireTime(): Long {
        val current = DateTime.current()
        val plusMonths = current.plusMonths(6)
        return plusMonths.timestamp
    }

    override fun refresh(token: SecurityToken): SecurityScope? {
        val scope = store.get(token)
        scope!!.expireTime = expireTime()
        return scope
    }
}
