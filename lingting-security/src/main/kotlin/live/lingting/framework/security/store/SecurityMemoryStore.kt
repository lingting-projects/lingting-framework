package live.lingting.framework.security.store

import live.lingting.framework.security.domain.SecurityScope
import java.util.concurrent.ConcurrentHashMap

/**
 * @author lingting 2023-06-15 16:07
 */
class SecurityMemoryStore : SecurityStore {
    private val map: MutableMap<String?, SecurityScope> = ConcurrentHashMap()

    override fun save(scope: SecurityScope) {
        map[scope.token] = scope
    }

    override fun update(scope: SecurityScope) {
        map[scope.token] = scope
    }

    override fun deleted(scope: SecurityScope) {
        map.remove(scope.token)
    }

    override fun get(token: String?): SecurityScope? {
        val scope = map[token]
        if (scope != null && scope.expireTime != null && System.currentTimeMillis() >= scope.expireTime) {
            map.remove(token)
            return null
        }
        return scope
    }
}
