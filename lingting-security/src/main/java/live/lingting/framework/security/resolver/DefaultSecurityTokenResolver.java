package live.lingting.framework.security.resolver;

import live.lingting.framework.Sequence;
import live.lingting.framework.security.domain.SecurityScope;
import live.lingting.framework.security.domain.SecurityToken;
import live.lingting.framework.security.store.SecurityStore;
import lombok.RequiredArgsConstructor;

/**
 * @author lingting 2024-05-27 16:36
 */
@RequiredArgsConstructor
public class DefaultSecurityTokenResolver implements SecurityTokenResolver, Sequence {

	private final SecurityStore store;

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
