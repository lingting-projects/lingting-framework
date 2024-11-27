package live.lingting.framework.security.store

import java.util.concurrent.ConcurrentHashMap
import live.lingting.framework.security.domain.SecurityScope
import live.lingting.framework.security.domain.SecurityToken
import live.lingting.framework.time.DateTime

/**
 * @author lingting 2023-06-15 16:07
 */
class SecurityMemoryStore : SecurityStore {
    private val map = ConcurrentHashMap<String, SecurityScope>()

    override fun save(scope: SecurityScope) {
        map[scope.authorization] = scope
    }

    override fun update(scope: SecurityScope) {
        map[scope.authorization] = scope
    }

    override fun deleted(scope: SecurityScope) {
        map.remove(scope.authorization)
    }

    override fun get(token: SecurityToken): SecurityScope? {
        val value = token.value
        val scope = map[value]
        if (scope != null && DateTime.millis() >= scope.expireTime) {
            map.remove(value)
            return null
        }
        return scope
    }
}
