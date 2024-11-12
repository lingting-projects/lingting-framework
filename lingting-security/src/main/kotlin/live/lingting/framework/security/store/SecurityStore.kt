package live.lingting.framework.security.store

import live.lingting.framework.security.domain.SecurityScope

/**
 * @author lingting 2023-04-06 15:55
 */
interface SecurityStore {
    fun save(scope: SecurityScope)

    fun update(scope: SecurityScope)

    fun deleted(scope: SecurityScope)

    fun get(token: String?): SecurityScope?
}
