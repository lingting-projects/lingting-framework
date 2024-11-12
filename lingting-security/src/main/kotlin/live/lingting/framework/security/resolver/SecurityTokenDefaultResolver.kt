package live.lingting.framework.security.resolver

import live.lingting.framework.Sequence
import live.lingting.framework.security.domain.SecurityScope
import live.lingting.framework.security.domain.SecurityToken
import live.lingting.framework.security.store.SecurityStore

/**
 * @author lingting 2024-05-27 16:36
 */
class SecurityTokenDefaultResolver(private val store: SecurityStore) : SecurityTokenResolver, Sequence {
    override fun isSupport(token: SecurityToken?): Boolean {
        return true
    }

    override fun resolver(token: SecurityToken): SecurityScope? {
        return store[token.token]
    }

    override val sequence: Int
        get() = Int.MAX_VALUE
}
