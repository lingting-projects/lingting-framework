package live.lingting.framework.security.resolver

import live.lingting.framework.Sequence
import live.lingting.framework.security.domain.SecurityScope
import live.lingting.framework.security.domain.SecurityToken

/**
 * @author lingting 2025/9/3 14:55
 */
class SecurityTokenResolverRegistry(resolvers: Collection<SecurityTokenResolver>) {

    private val resolvers: List<SecurityTokenResolver> = Sequence.asc(resolvers)

    fun resolver(token: SecurityToken): SecurityScope? {
        return resolvers.find { it.isSupport(token) }?.resolver(token)
    }

}
