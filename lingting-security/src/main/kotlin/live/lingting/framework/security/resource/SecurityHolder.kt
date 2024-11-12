package live.lingting.framework.security.resource;

import live.lingting.framework.security.domain.SecurityScope;
import live.lingting.framework.thread.StackThreadLocal;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

/**
 * @author lingting 2023-03-29 20:29
 */
public final class SecurityHolder {

	static final StackThreadLocal<SecurityScope> LOCAL = new StackThreadLocal<>();

	private SecurityHolder() {throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");}

	public static void put(SecurityScope scope) {
		LOCAL.put(scope);
	}

	public static void pop() {
		LOCAL.pop();
	}

	public static SecurityScope get() {
		return scope();
	}

	public static Optional<SecurityScope> option() {
		return scopeOption();
	}

	public static SecurityScope scope() {
		return LOCAL.get();
	}

	public static Optional<SecurityScope> scopeOption() {
		return Optional.ofNullable(scope());
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
