package live.lingting.framework.security.grpc

import live.lingting.framework.convert.SecurityGrpcConvert
import live.lingting.framework.security.domain.AuthorizationVO
import live.lingting.framework.security.domain.SecurityScope
import live.lingting.framework.security.domain.SecurityScopeAttributes
import live.lingting.framework.util.BooleanUtils
import live.lingting.framework.util.CollectionUtils

/**
 * @author lingting 2024-01-30 20:19
 */
class SecurityGrpcExpandConvert : SecurityGrpcConvert() {
    override fun scopeExpand(scope: SecurityScope): ExpandSecurityScope {
        val expand = ExpandSecurityScope()
        expand.from(scope)
        val attributes = scope.attributes
        expand.isExpand = isExpand(attributes)
        return expand
    }

    override fun voExpand(vo: AuthorizationVO): ExpandAuthorizationVO {
        val expand = ExpandAuthorizationVO()
        expand.from(vo)
        val attributes = vo.attributes
        expand.isExpand = isExpand(attributes)
        return expand
    }

    fun isExpand(attributes: SecurityScopeAttributes): Boolean {
        if (!CollectionUtils.isEmpty(attributes) && attributes.containsKey("expand")) {
            return BooleanUtils.isTrue(attributes["expand"])
        }
        return false
    }

}
