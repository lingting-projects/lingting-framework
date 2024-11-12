package live.lingting.framework.security.resource;

import live.lingting.framework.Sequence;
import live.lingting.framework.security.domain.SecurityScope;
import live.lingting.framework.security.domain.SecurityToken;
import live.lingting.framework.security.resolver.SecurityTokenResolver;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lingting 2023-12-15 15:57
 */
public class SecurityDefaultResourceServiceImpl implements SecurityResourceService {

	private final List<SecurityTokenResolver> resolvers;

	public SecurityDefaultResourceServiceImpl(List<SecurityTokenResolver> resolvers) {
		ArrayList<SecurityTokenResolver> list = new ArrayList<>(resolvers);
		Sequence.asc(list);
		this.resolvers = list;
	}

	@Override
	public SecurityScope resolve(SecurityToken token) {
		return resolvers.stream().filter(r -> r.isSupport(token)).findFirst().map(r -> r.resolver(token)).orElse(null);
	}

}
