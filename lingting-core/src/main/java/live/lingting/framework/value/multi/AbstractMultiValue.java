package live.lingting.framework.value.multi;

import live.lingting.framework.value.MultiValue;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * @author lingting 2024-09-05 20:33
 */
@RequiredArgsConstructor
public abstract class AbstractMultiValue<K, V, C extends Collection<V>> implements MultiValue<K, V, C> {

	protected final Map<K, C> map = new ConcurrentHashMap<>();

	protected final Supplier<C> supplier;

	protected C absent(K key) {
		return map.computeIfAbsent(key, k -> supplier.get());
	}

	@Override
	public void ifAbsent(K key) {
		absent(key);
	}

	// region fill
	@Override
	public void add(K key, V value) {
		addAll(key, value);
	}

	@Override
	public void addAll(K key, Collection<V> values) {
		absent(key).addAll(values);
	}

	@SafeVarargs
	public final void addAll(K key, V... values) {
		absent(key).addAll(Arrays.asList(values));
	}

	@Override
	public void addAll(K key, Iterable<V> values) {
		C c = absent(key);
		values.forEach(c::add);
	}

	@Override
	public void addAll(Map<K, Collection<V>> map) {
		map.forEach(this::addAll);
	}

	@Override
	public void addAll(AbstractMultiValue<K, V, C> value) {
		value.forEach((this::addAll));
	}

	@Override
	public void put(K key, V value) {
		putAll(key, value);
	}

	@SafeVarargs
	public final void putAll(K key, V... values) {
		C c = supplier.get();
		c.addAll(Arrays.asList(values));
		map.put(key, c);
	}

	@Override
	public void putAll(K key, Iterable<V> values) {
		C c = supplier.get();
		values.forEach(c::add);
		map.put(key, c);
	}

	@Override
	public void putAll(Map<K, Collection<V>> map) {
		map.forEach(this::putAll);
	}

	@Override
	public void putAll(AbstractMultiValue<K, V, C> value) {
		value.forEach((this::putAll));
	}

	// endregion

	// region get

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public Collection<V> get(K key) {
		return absent(key);
	}

	@Override
	public Iterator<V> iterator(K key) {
		return get(key).iterator();
	}

	@Override
	public V first(K key) {
		return get(key).iterator().next();
	}

	@Override
	public Set<K> keys() {
		return map.keySet();
	}

	@Override
	public Collection<C> values() {
		return map.values();
	}

	@Override
	public Set<Map.Entry<K, C>> entries() {
		return map.entrySet();
	}

	@Override
	public UnmodifiableMultiValue<K, V> unmodifiable() {
		return new UnmodifiableMultiValue<>(this);
	}

	// endregion

	// region remove
	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public C remove(K key) {
		absent(key);
		return map.remove(key);
	}

	@Override
	public boolean remove(K key, V value) {
		C absent = absent(key);
		return absent.remove(value);
	}

	// endregion

	// region function
	@Override
	public void forEach(BiConsumer<K, C> consumer) {
		map.forEach(consumer);
	}

	@Override
	public void each(BiConsumer<K, V> consumer) {
		forEach((k, c) -> c.forEach(v -> consumer.accept(k, v)));
	}

	// endregion

}
