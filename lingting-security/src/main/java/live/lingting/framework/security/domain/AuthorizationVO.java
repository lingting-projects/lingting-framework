package live.lingting.framework.security.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

/**
 * @author lingting 2023-03-30 13:54
 */
@Getter
@Setter
public class AuthorizationVO {

	private String token;

	private String tenantId;

	private String userId;

	private String username;

	private String avatar;

	private String nickname;

	/**
	 * 是否启用
	 */
	private Boolean enabled;

	private Set<String> roles;

	private Set<String> permissions;

	private SecurityScopeAttributes attributes;

}
