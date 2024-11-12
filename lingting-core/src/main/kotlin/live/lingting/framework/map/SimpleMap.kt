package live.lingting.framework.map;

import live.lingting.framework.util.BooleanUtils;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author lingting 2024-04-20 16:17
 */
public interface SimpleMap<K, V> extends Map<K, V> {

	default V find(K key) {
		return isEmpty() ? null : get(key);
	}

	default <T> T find(K key, Function<Optional<V>, T> func) {
		V v = find(key);
		Optional<V> optional = Optional.ofNullable(v);
		return func.apply(optional);
	}

	default <T> T find(K key, T defaultValue, Function<V, T> func) {
		return find(key, defaultValue, Objects::isNull, func);
	}

	/**
	 * @param usingDefault 如果返回true表示使用默认值
	 */
	default <T> T find(K key, T defaultValue, Predicate<V> usingDefault, Function<V, T> func) {
		V v = find(key);
		if (v == null || usingDefault.test(v)) {
			return defaultValue;
		}
		return func.apply(v);
	}

	default boolean toBoolean(K key) {
		return find(key, false, BooleanUtils::isTrue);
	}

}
