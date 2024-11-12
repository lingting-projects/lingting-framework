package live.lingting.framework.security.grpc.authorization;

import live.lingting.framework.security.authorize.SecurityAuthorizationService;
import live.lingting.framework.security.domain.SecurityScope;
import live.lingting.framework.security.domain.SecurityScopeAttributes;
import live.lingting.framework.security.exception.AuthorizationException;
import live.lingting.framework.security.store.SecurityStore;
import live.lingting.framework.util.LocalDateTimeUtils;
import live.lingting.framework.util.MdcUtils;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

/**
 * @author lingting 2024-01-30 20:30
 */
public class AuthorizationServiceImpl implements SecurityAuthorizationService {

	private final SecurityStore store;

	public AuthorizationServiceImpl(SecurityStore store) {
		this.store = store;
	}

	@Override
	public SecurityScope validAndBuildScope(String username, String password) throws AuthorizationException {
		if (!Objects.equals(username, "user") && !Objects.equals(username, "admin")) {
			throw new AuthorizationException();
		}
		SecurityScope scope = new SecurityScope();
		scope.setToken(username);
		scope.setTenantId(username);
		scope.setUserId(username);
		scope.setUsername(username);
		scope.setPassword(password);
		scope.setAvatar("");
		scope.setNickname(username);
		scope.setEnabled(true);
		scope.setExpireTime(expireTime());
		scope.setRoles(new HashSet<>(List.of(username)));
		scope.setPermissions(new HashSet<>(List.of(username)));
		SecurityScopeAttributes attributes = new SecurityScopeAttributes();
		attributes.put("expand", "true");
		attributes.put("tag", MdcUtils.traceId());
		scope.setAttributes(attributes);
		return scope;
	}

	Long expireTime() {
		return LocalDateTimeUtils.toTimestamp(LocalDateTime.now().plusMonths(6));
	}

	@Override
	public SecurityScope refresh(String token) {
		SecurityScope scope = store.get(token);
		scope.setExpireTime(expireTime());
		return scope;
	}

}
