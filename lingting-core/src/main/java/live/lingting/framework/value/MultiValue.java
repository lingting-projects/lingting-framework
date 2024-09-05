package live.lingting.framework.value;

import live.lingting.framework.value.multi.AbstractMultiValue;
import live.lingting.framework.value.multi.UnmodifiableMultiValue;

import java.util.Collection;
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

	void add(K key, V value);

	void addAll(K key, Collection<V> values);

	void addAll(K key, Iterable<V> values);

	void addAll(Map<K, Collection<V>> map);

	void addAll(AbstractMultiValue<K, V, C> value);

	void put(K key, V value);

	void putAll(K key, Iterable<V> values);

	void putAll(Map<K, Collection<V>> map);

	void putAll(AbstractMultiValue<K, V, C> value);

	// endregion
	// region get

	boolean isEmpty();

	boolean isEmpty(K key);

	boolean hasKey(K key);

	Collection<V> get(K key);

	Iterator<V> iterator(K key);

	V first(K key);

	Set<K> keys();

	Collection<C> values();

	Set<Map.Entry<K, C>> entries();

	UnmodifiableMultiValue<K, V> unmodifiable();

	// endregion
	// region remove
	void clear();

	C remove(K key);

	boolean remove(K key, V value);

	// region function
	void forEach(BiConsumer<K, C> consumer);

	void each(BiConsumer<K, V> consumer);

	// endregion

}
