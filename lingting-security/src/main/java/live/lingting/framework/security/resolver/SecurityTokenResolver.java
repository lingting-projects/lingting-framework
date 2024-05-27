package live.lingting.framework.security.resolver;

import live.lingting.framework.security.domain.SecurityScope;
import live.lingting.framework.security.domain.SecurityToken;

/**
 * @author lingting 2024-05-27 16:36
 */
public interface SecurityTokenResolver {
	boolean isSupport(SecurityToken token);

	SecurityScope resolver(SecurityToken token);
}
