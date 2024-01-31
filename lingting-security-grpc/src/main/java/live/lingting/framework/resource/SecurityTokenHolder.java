package live.lingting.framework.resource;

import live.lingting.framework.security.domain.SecurityToken;
import lombok.experimental.UtilityClass;

/**
 * @author lingting 2023-12-18 16:39
 */
@UtilityClass
public class SecurityTokenHolder {

	private static final ThreadLocal<SecurityToken> THREAD_LOCAL = new ThreadLocal<>();

	public static SecurityToken get() {
		return THREAD_LOCAL.get();
	}

	public static void set(SecurityToken value) {
		THREAD_LOCAL.set(value);
	}

	public static void remove() {
		THREAD_LOCAL.remove();
	}

}
