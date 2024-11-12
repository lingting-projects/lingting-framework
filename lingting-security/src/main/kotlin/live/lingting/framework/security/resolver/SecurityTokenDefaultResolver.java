package live.lingting.framework.security.resolver;

import live.lingting.framework.Sequence;
import live.lingting.framework.security.domain.SecurityScope;
import live.lingting.framework.security.domain.SecurityToken;
import live.lingting.framework.security.store.SecurityStore;

/**
 * @author lingting 2024-05-27 16:36
 */
public class SecurityTokenDefaultResolver implements SecurityTokenResolver, Sequence {

	private final SecurityStore store;

	public SecurityTokenDefaultResolver(SecurityStore store) {
		this.store = store;
	}

	@Override
	public boolean isSupport(SecurityToken token) {
		return true;
	}

	@Override
	public SecurityScope resolver(SecurityToken token) {
		return store.get(token.getToken());
	}

	@Override
	public int getSequence() {
		return Integer.MAX_VALUE;
	}

}
