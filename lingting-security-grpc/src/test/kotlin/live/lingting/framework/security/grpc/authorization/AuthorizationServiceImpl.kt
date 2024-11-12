package live.lingting.framework.security.grpc.authorization

import live.lingting.framework.security.authorize.SecurityAuthorizationService
import live.lingting.framework.security.domain.SecurityScope
import live.lingting.framework.security.domain.SecurityScopeAttributes
import live.lingting.framework.security.exception.AuthorizationException
import live.lingting.framework.security.store.SecurityStore
import live.lingting.framework.util.LocalDateTimeUtils
import live.lingting.framework.util.MdcUtils
import java.time.LocalDateTime
import java.util.List

/**
 * @author lingting 2024-01-30 20:30
 */
class AuthorizationServiceImpl(private val store: SecurityStore) : SecurityAuthorizationService {
    @Throws(AuthorizationException::class)
    override fun validAndBuildScope(username: String?, password: String?): SecurityScope? {
        if (username != "user" && username != "admin") {
            throw AuthorizationException()
        }
        val scope = SecurityScope()
        scope.token = username
        scope.tenantId = username
        scope.userId = username
        scope.username = username
        scope.password = password
        scope.avatar = ""
        scope.nickname = username
        scope.enabled = true
        scope.expireTime = expireTime()
        scope.setRoles(HashSet<E>(List.of<E>(username)))
        scope.setPermissions(HashSet<E>(List.of<E>(username)))
        val attributes = SecurityScopeAttributes()
        attributes["expand"] = "true"
        attributes["tag"] = MdcUtils.traceId()
        scope.attributes = attributes
        return scope
    }

    fun expireTime(): Long {
        return LocalDateTimeUtils.toTimestamp(LocalDateTime.now().plusMonths(6))
    }

    override fun refresh(token: String?): SecurityScope? {
        val scope = store.get(token)
        scope!!.expireTime = expireTime()
        return scope
    }
}
