package live.lingting.framework.security.resource;

import live.lingting.framework.security.domain.SecurityScope;
import live.lingting.framework.security.domain.SecurityToken;
import live.lingting.framework.security.store.SecurityStore;
import lombok.RequiredArgsConstructor;

/**
 * @author lingting 2023-12-15 15:57
 */
@RequiredArgsConstructor
public class SecurityDefaultResourceServiceImpl implements SecurityResourceService {

	private final SecurityStore store;

	@Override
	public SecurityScope resolve(SecurityToken token) {
		return store.get(token.getToken());
	}

}
