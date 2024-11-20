package live.lingting.framework.security.resource

import live.lingting.framework.Sequence
import live.lingting.framework.security.domain.SecurityScope
import live.lingting.framework.security.domain.SecurityToken
import live.lingting.framework.security.resolver.SecurityTokenResolver

/**
 * @author lingting 2023-12-15 15:57
 */
class SecurityDefaultResourceServiceImpl(resolvers: List<SecurityTokenResolver>) : SecurityResourceService {
    private val resolvers: List<SecurityTokenResolver>

    init {
        val list = ArrayList(resolvers)
        Sequence.asc(list)
        this.resolvers = list
    }

    override fun resolve(token: SecurityToken): SecurityScope? {
        return resolvers.find { it.isSupport(token) }?.resolver(token)
    }

}
