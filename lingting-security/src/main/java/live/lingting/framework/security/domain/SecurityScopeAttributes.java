package live.lingting.framework.security.domain;

import java.util.HashMap;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author lingting 2024-04-20 15:56
 */
public class SecurityScopeAttributes extends HashMap<String, Object> {

	public Object find(String key) {
		return isEmpty() ? null : get(key);
	}

	public <T> T find(String key, Function<Optional<Object>, T> func) {
		return func.apply(Optional.ofNullable(find(key)));
	}

	public <T> T find(String key, T defaultValue, Function<Object, T> func) {
		return find(key, defaultValue, Optional::isEmpty, func);
	}

	/**
	 * @param usingDefault 如果返回true表示使用默认值
	 */
	public <T> T find(String key, T defaultValue, Predicate<Optional<Object>> usingDefault, Function<Object, T> func) {
		Optional<Object> optional = Optional.ofNullable(find(key));
		if (usingDefault.test(optional)) {
			return defaultValue;
		}
		return func.apply(optional);
	}

}
