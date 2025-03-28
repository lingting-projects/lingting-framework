package live.lingting.framework.security.grpc

import live.lingting.framework.security.domain.AuthorizationVO
import live.lingting.framework.security.domain.SecurityScope
import live.lingting.framework.security.domain.SecurityScopeAttributes
import live.lingting.framework.security.grpc.convert.SecurityGrpcConvert
import live.lingting.framework.util.BooleanUtils.isTrue

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
        if (!attributes.isNullOrEmpty() && attributes.containsKey("expand")) {
            return attributes["expand"].isTrue()
        }
        return false
    }

}
