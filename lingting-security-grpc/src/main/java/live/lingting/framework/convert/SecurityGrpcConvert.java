package live.lingting.framework.convert;

import com.google.protobuf.ByteString;
import live.lingting.framework.jackson.JacksonUtils;
import live.lingting.framework.security.convert.SecurityConvert;
import live.lingting.framework.security.domain.AuthorizationVO;
import live.lingting.framework.security.domain.SecurityScopeAttributes;
import live.lingting.framework.util.CollectionUtils;
import live.lingting.framework.util.StringUtils;
import live.lingting.protobuf.SecurityGrpcAuthorization;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;

/**
 * @author lingting 2023-12-18 16:04
 */
public class SecurityGrpcConvert implements SecurityConvert {

	public SecurityGrpcAuthorization.AuthorizationVO toProtobuf(AuthorizationVO vo) {
		SecurityGrpcAuthorization.AuthorizationVO.Builder builder = SecurityGrpcAuthorization.AuthorizationVO
			.newBuilder()
			.setToken(vo.getToken())
			.setTenantId(vo.getTenantId())
			.setUserId(vo.getUserId())
			.setUsername(vo.getUsername())
			.setAvatar(vo.getAvatar())
			.setNickname(vo.getNickname())
			.setIsEnabled(Boolean.TRUE.equals(vo.getEnabled()));

		if (!CollectionUtils.isEmpty(vo.getRoles())) {
			builder.addAllRoles(vo.getRoles());
		}
		if (!CollectionUtils.isEmpty(vo.getPermissions())) {
			builder.addAllPermissions(vo.getPermissions());
		}

		builder.setAttributes(toBytes(vo.getAttributes()));
		return builder.buildPartial();
	}

	public AuthorizationVO toJava(SecurityGrpcAuthorization.AuthorizationVO authorizationVO) {
		AuthorizationVO vo = new AuthorizationVO();
		vo.setToken(authorizationVO.getToken());
		vo.setTenantId(authorizationVO.getTenantId());
		vo.setUserId(authorizationVO.getUserId());
		vo.setUsername(authorizationVO.getUsername());
		vo.setAvatar(authorizationVO.getAvatar());
		vo.setNickname(authorizationVO.getNickname());
		vo.setEnabled(authorizationVO.getIsEnabled());
		vo.setRoles(new HashSet<>(authorizationVO.getRolesList()));
		vo.setPermissions(new HashSet<>(authorizationVO.getPermissionsList()));
		vo.setAttributes(ofBytes(authorizationVO.getAttributes()));
		return vo;
	}

	public Charset charset() {
		return StandardCharsets.UTF_8;
	}

	public ByteString toBytes(SecurityScopeAttributes attributes) {
		if (CollectionUtils.isEmpty(attributes)) {
			return ByteString.EMPTY;
		}
		String json = JacksonUtils.toJson(attributes);
		Charset charset = charset();
		return ByteString.copyFrom(json, charset);
	}

	public SecurityScopeAttributes ofBytes(ByteString bytes) {
		if (bytes.isEmpty()) {
			return new SecurityScopeAttributes();
		}
		Charset charset = charset();
		String json = bytes.toString(charset);

		if (!StringUtils.hasText(json)) {
			return new SecurityScopeAttributes();
		}

		return JacksonUtils.toObj(json, SecurityScopeAttributes.class);
	}

}
