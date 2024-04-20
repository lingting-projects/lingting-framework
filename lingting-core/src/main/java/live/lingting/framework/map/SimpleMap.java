package live.lingting.framework.map;

import live.lingting.framework.util.BooleanUtils;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author lingting 2024-04-20 16:17
 */
public interface SimpleMap<K, V> extends Map<K, V> {

	default Object find(String key) {
		return isEmpty() ? null : get(key);
	}

	default <T> T find(String key, Function<Optional<Object>, T> func) {
		return func.apply(Optional.ofNullable(find(key)));
	}

	default <T> T find(String key, T defaultValue, Function<Object, T> func) {
		return find(key, defaultValue, Optional::isEmpty, func);
	}

	/**
	 * @param usingDefault 如果返回true表示使用默认值
	 */
	default <T> T find(String key, T defaultValue, Predicate<Optional<Object>> usingDefault, Function<Object, T> func) {
		Optional<Object> optional = Optional.ofNullable(find(key));
		if (usingDefault.test(optional)) {
			return defaultValue;
		}
		return func.apply(optional);
	}

	default boolean toBoolean(String key) {
		return find(key, false, BooleanUtils::isTrue);
	}

}
