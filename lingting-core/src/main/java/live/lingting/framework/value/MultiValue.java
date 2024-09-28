package live.lingting.framework.value;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * @author lingting 2024-09-05 21:17
 */
public interface MultiValue<K, V, C extends Collection<V>> {

	// region fill

	/**
	 * 为指定key创建一个槽位(如果不存在)
	 */
	void ifAbsent(K key);

	void add(K key);

	void add(K key, V value);

	void addAll(K key, Collection<V> values);

	void addAll(K key, Iterable<V> values);

	void addAll(Map<K, ? extends Collection<V>> map);

	void addAll(MultiValue<K, V, C> value);

	void put(K key, V value);

	void putAll(K key, Iterable<V> values);

	void putAll(Map<K, Collection<V>> map);

	void putAll(MultiValue<K, V, C> value);

	// endregion

	// region get

	boolean isEmpty();

	boolean isEmpty(K key);

	int size();

	boolean hasKey(K key);

	C get(K key);

	Iterator<V> iterator(K key);

	V first(K key);

	default V first(K key, V defaultValue) {
		V v = first(key);
		return v == null ? defaultValue : v;
	}

	Set<K> keys();

	Collection<C> values();

	Map<K, C> map();

	Set<Map.Entry<K, C>> entries();

	MultiValue<K, V, Collection<V>> unmodifiable();

	// endregion

	// region remove
	void clear();

	C remove(K key);

	boolean remove(K key, V value);

	// endregion

	// region function

	void forEach(BiConsumer<K, C> consumer);

	void each(BiConsumer<K, V> consumer);

	default void forEachSorted(BiConsumer<K, C> consumer) {
		keys().stream().sorted().forEach(key -> consumer.accept(key, get(key)));
	}

	default void forEachSorted(BiConsumer<K, C> consumer, Comparator<K> comparator) {
		keys().stream().sorted(comparator).forEach(key -> consumer.accept(key, get(key)));
	}

	default void eachSorted(BiConsumer<K, V> consumer) {
		forEachSorted((k, c) -> c.forEach(v -> consumer.accept(k, v)));
	}

	default void eachSorted(BiConsumer<K, V> consumer, Comparator<K> comparator) {
		forEachSorted((k, c) -> c.forEach(v -> consumer.accept(k, v)), comparator);
	}

	// endregion

}
