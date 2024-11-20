package live.lingting.framework.convert

import com.google.protobuf.ByteString
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import live.lingting.framework.jackson.JacksonUtils
import live.lingting.framework.protobuf.SecurityGrpcAuthorization
import live.lingting.framework.security.convert.SecurityConvert
import live.lingting.framework.security.domain.AuthorizationVO
import live.lingting.framework.security.domain.SecurityScopeAttributes
import live.lingting.framework.util.CollectionUtils
import live.lingting.framework.util.StringUtils

/**
 * @author lingting 2023-12-18 16:04
 */
open class SecurityGrpcConvert : SecurityConvert {
    fun toProtobuf(vo: AuthorizationVO): SecurityGrpcAuthorization.AuthorizationVO {
        val builder = SecurityGrpcAuthorization.AuthorizationVO
            .newBuilder()
            .setToken(vo.token)
            .setTenantId(vo.tenantId)
            .setUserId(vo.userId)
            .setUsername(vo.username)
            .setAvatar(vo.avatar)
            .setNickname(vo.nickname)
            .setIsEnabled(java.lang.Boolean.TRUE == vo.enabled)

        if (!CollectionUtils.isEmpty(vo.roles)) {
            builder.addAllRoles(vo.roles)
        }
        if (!CollectionUtils.isEmpty(vo.permissions)) {
            builder.addAllPermissions(vo.permissions)
        }

        builder.setAttributes(toBytes(vo.attributes))
        return builder.buildPartial()
    }

    fun toJava(authorizationVO: SecurityGrpcAuthorization.AuthorizationVO): AuthorizationVO {
        val vo = AuthorizationVO()
        vo.token = authorizationVO.token
        vo.tenantId = authorizationVO.tenantId
        vo.userId = authorizationVO.userId
        vo.username = authorizationVO.username
        vo.avatar = authorizationVO.avatar
        vo.nickname = authorizationVO.nickname
        vo.enabled = authorizationVO.isEnabled
        vo.roles = authorizationVO.rolesList.toSet()
        vo.permissions = authorizationVO.permissionsList.toSet()
        vo.attributes = ofBytes(authorizationVO.attributes)
        return vo
    }

    fun charset(): Charset {
        return StandardCharsets.UTF_8
    }

    fun toBytes(attributes: SecurityScopeAttributes?): ByteString {
        if (CollectionUtils.isEmpty(attributes)) {
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
}
