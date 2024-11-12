package live.lingting.framework.security.resource

import live.lingting.framework.security.domain.SecurityScope
import live.lingting.framework.security.domain.SecurityToken

/**
 * 资源服务用
 *
 * @author lingting 2023-03-29 21:19
 */
interface SecurityResourceService {
    fun resolve(token: SecurityToken): SecurityScope?

    fun putScope(scope: SecurityScope) {
        SecurityHolder.Companion.put(scope)
    }

    fun popScope() {
        SecurityHolder.Companion.pop()
    }
}
