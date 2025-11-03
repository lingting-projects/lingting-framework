package live.lingting.framework.security.resource

import live.lingting.framework.security.domain.SecurityScope
import live.lingting.framework.security.domain.SecurityToken
import live.lingting.framework.security.resolver.SecurityTokenResolver
import live.lingting.framework.security.resolver.SecurityTokenResolverRegistry

/**
 * @author lingting 2023-12-15 15:57
 */
class SecurityDefaultResourceServiceImpl : SecurityResourceService {

    constructor(resolvers: Collection<SecurityTokenResolver>) : this(SecurityTokenResolverRegistry(resolvers))

    constructor(registry: SecurityTokenResolverRegistry) {
        this.registry = registry
    }

    val registry: SecurityTokenResolverRegistry

    override fun resolve(token: SecurityToken): SecurityScope? {
        return registry.resolver(token)
    }

}
