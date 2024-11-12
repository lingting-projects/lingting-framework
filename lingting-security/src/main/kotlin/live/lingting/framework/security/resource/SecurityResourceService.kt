package live.lingting.framework.security.resource;

import live.lingting.framework.security.domain.SecurityScope;
import live.lingting.framework.security.domain.SecurityToken;

/**
 * 资源服务用
 *
 * @author lingting 2023-03-29 21:19
 */
public interface SecurityResourceService {

	SecurityScope resolve(SecurityToken token);

	default void putScope(SecurityScope scope) {
		SecurityHolder.put(scope);
	}

	default void popScope() {
		SecurityHolder.pop();
	}

}
