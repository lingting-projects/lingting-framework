package live.lingting.framework.security.resource;

import live.lingting.framework.security.domain.SecurityScope;
import lombok.experimental.UtilityClass;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

/**
 * @author lingting 2023-03-29 20:29
 */
@UtilityClass
public class SecurityHolder {

	static final ThreadLocal<SecurityScope> SCOPE_THREAD_LOCAL = new ThreadLocal<>();

	public static void set(SecurityScope scope) {
		SCOPE_THREAD_LOCAL.set(scope);
	}

	public static void clear() {
		SCOPE_THREAD_LOCAL.remove();
	}

	public static SecurityScope get() {
		return scope();
	}

	public static Optional<SecurityScope> option() {
		return scopeOption();
	}

	public static SecurityScope scope() {
		return SCOPE_THREAD_LOCAL.get();
	}

	public static Optional<SecurityScope> scopeOption() {
		return Optional.ofNullable(scope());
	}

	public static boolean isSystem() {
		return scopeOption().map(SecurityScope::isSystem).orElse(true);
	}

	public static String token() {
		return scopeOption().map(SecurityScope::getToken).orElse("");
	}

	public static String userId() {
		return scopeOption().map(SecurityScope::getUserId).orElse(null);
	}

	public static String tenantId() {
		return scopeOption().map(SecurityScope::getTenantId).orElse(null);
	}

	public static String username() {
		return scopeOption().map(SecurityScope::getUsername).orElse("");
	}

	public static String password() {
		return scopeOption().map(SecurityScope::getPassword).orElse("");
	}

	public static Set<String> roles() {
		return scopeOption().map(SecurityScope::getRoles).orElse(Collections.emptySet());
	}

	public static Set<String> permissions() {
		return scopeOption().map(SecurityScope::getPermissions).orElse(Collections.emptySet());
	}

}
