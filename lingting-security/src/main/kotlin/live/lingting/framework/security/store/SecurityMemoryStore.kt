package live.lingting.framework.security.store;

import live.lingting.framework.security.domain.SecurityScope;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lingting 2023-06-15 16:07
 */
public class SecurityMemoryStore implements SecurityStore {

	private final Map<String, SecurityScope> map = new ConcurrentHashMap<>();

	@Override
	public void save(SecurityScope scope) {
		map.put(scope.getToken(), scope);
	}

	@Override
	public void update(SecurityScope scope) {
		map.put(scope.getToken(), scope);
	}

	@Override
	public void deleted(SecurityScope scope) {
		map.remove(scope.getToken());
	}

	@Override
	public SecurityScope get(String token) {
		SecurityScope scope = map.get(token);
		if (scope != null && scope.getExpireTime() != null && System.currentTimeMillis() >= scope.getExpireTime()) {
			map.remove(token);
			return null;
		}
		return scope;
	}

}
