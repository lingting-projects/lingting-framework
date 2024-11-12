package live.lingting.framework.security.grpc

import live.lingting.framework.convert.SecurityGrpcConvert
import live.lingting.framework.security.domain.AuthorizationVO
import live.lingting.framework.security.domain.SecurityScope
import live.lingting.framework.security.domain.SecurityScopeAttributes
import live.lingting.framework.security.grpc.SecurityGrpcExpandConvert.ExpandMapstruct
import live.lingting.framework.util.BooleanUtils
import live.lingting.framework.util.CollectionUtils
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

/**
 * @author lingting 2024-01-30 20:19
 */
class SecurityGrpcExpandConvert : SecurityGrpcConvert() {
    override fun scopeExpand(scope: SecurityScope): ExpandSecurityScope {
        val expand = ExpandMapstruct.INSTANCE.of(scope)
        val attributes = scope.attributes
        expand.isExpand = isExpand(attributes!!)
        return expand
    }

    override fun voExpand(vo: AuthorizationVO): ExpandAuthorizationVO {
        val expand = ExpandMapstruct.INSTANCE.of(vo)
        val attributes = vo.attributes
        expand.isExpand = isExpand(attributes!!)
        return expand
    }

    fun isExpand(attributes: SecurityScopeAttributes): Boolean {
        if (!CollectionUtils.isEmpty(attributes) && attributes.containsKey("expand")) {
            return BooleanUtils.isTrue(attributes["expand"]!!)
        }
        return false
    }

    @Mapper
    interface ExpandMapstruct {
        fun of(vo: AuthorizationVO?): ExpandAuthorizationVO

        fun of(scope: SecurityScope?): ExpandSecurityScope

        companion object {
            val INSTANCE: ExpandMapstruct = Mappers.getMapper(ExpandMapstruct::class.java)
        }
    }
}
