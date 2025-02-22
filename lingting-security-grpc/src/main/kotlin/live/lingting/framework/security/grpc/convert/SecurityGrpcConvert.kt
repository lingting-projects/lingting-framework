package live.lingting.framework.security.grpc.convert

import com.google.protobuf.ByteString
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import live.lingting.framework.jackson.JacksonUtils
import live.lingting.framework.protobuf.SecurityGrpcAuthorization
import live.lingting.framework.security.convert.SecurityConvert
import live.lingting.framework.security.domain.AuthorizationVO
import live.lingting.framework.security.domain.SecurityScopeAttributes
import live.lingting.framework.security.domain.SecurityToken
import live.lingting.framework.security.po.EndpointTokenPO
import live.lingting.framework.util.StringUtils

/**
 * @author lingting 2023-12-18 16:04
 */
open class SecurityGrpcConvert : SecurityConvert {

    fun toProtobuf(vo: AuthorizationVO): SecurityGrpcAuthorization.AuthorizationVO {
        val builder = SecurityGrpcAuthorization.AuthorizationVO
            .newBuilder()
            .setAuthorization(vo.authorization)
            .setTenantId(vo.tenantId)
            .setUserId(vo.userId)
            .setUsername(vo.username)
            .setAvatar(vo.avatar)
            .setNickname(vo.nickname)
            .setEnabled(true == vo.enabled)

        if (vo.roles.isNotEmpty()) {
            builder.addAllRoles(vo.roles)
        }
        if (vo.permissions.isNotEmpty()) {
            builder.addAllPermissions(vo.permissions)
        }

        builder.setAttributes(toBytes(vo.attributes))
        return builder.buildPartial()
    }

    fun toJava(authorizationVO: SecurityGrpcAuthorization.AuthorizationVO): AuthorizationVO {
        val vo = AuthorizationVO()
        vo.authorization = authorizationVO.authorization
        vo.tenantId = authorizationVO.tenantId
        vo.userId = authorizationVO.userId
        vo.username = authorizationVO.username
        vo.avatar = authorizationVO.avatar
        vo.nickname = authorizationVO.nickname
        vo.enabled = authorizationVO.enabled
        vo.roles = authorizationVO.rolesList.toSet()
        vo.permissions = authorizationVO.permissionsList.toSet()
        vo.attributes = ofBytes(authorizationVO.attributes)
        return vo
    }

    fun charset(): Charset {
        return StandardCharsets.UTF_8
    }

    fun toBytes(attributes: SecurityScopeAttributes?): ByteString {
        if (attributes.isNullOrEmpty()) {
            return ByteString.EMPTY
        }
        val json = JacksonUtils.toJson(attributes)
        val charset = charset()
        return ByteString.copyFrom(json, charset)
    }

    fun ofBytes(bytes: ByteString): SecurityScopeAttributes {
        if (bytes.isEmpty) {
            return SecurityScopeAttributes()
        }
        val charset = charset()
        val json = bytes.toString(charset)

        if (!StringUtils.hasText(json)) {
            return SecurityScopeAttributes()
        }

        return JacksonUtils.toObj(json, SecurityScopeAttributes::class.java)
    }

    fun toToken(po: SecurityGrpcAuthorization.TokenPO): SecurityToken {
        val p = EndpointTokenPO().also {
            it.raw = po.raw
            it.type = po.type
            it.value = po.value
        }
        return toToken(p)
    }

}
