package live.lingting.framework.value.multi;

import live.lingting.framework.util.CollectionUtils;
import live.lingting.framework.value.MultiValue;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author lingting 2024-09-05 20:33
 */
public abstract class AbstractMultiValue<K, V, C extends Collection<V>> implements MultiValue<K, V, C> {

	protected final Map<K, C> map = new ConcurrentHashMap<>();

	protected final boolean allowModify;

	protected final Supplier<C> supplier;

	protected AbstractMultiValue(Supplier<C> supplier) {
		this(true, supplier);
	}

	protected AbstractMultiValue(boolean allowModify, Supplier<C> supplier) {
		this.allowModify = allowModify;
		this.supplier = supplier;
	}

	protected K convert(K key) {
		return key;
	}

	protected C absent(K key) {
		if (!allowModify && !hasKey(key)) {
			throw new UnsupportedOperationException();
		}
		key = convert(key);
		return map.computeIfAbsent(key, k -> supplier.get());
	}

	@Override
	public void ifAbsent(K key) {
		absent(key);
	}

	// region fill

	@Override
	public void add(K key) {
		if (!allowModify) {
			throw new UnsupportedOperationException();
		}
		absent(key);
	}

	@Override
	public void add(K key, V value) {
		if (!allowModify) {
			throw new UnsupportedOperationException();
		}
		absent(key).add(value);
	}

	@Override
	public void addAll(K key, Collection<V> values) {
		if (!allowModify) {
			throw new UnsupportedOperationException();
		}
		absent(key).addAll(values);
	}

	@Override
	public void addAll(K key, Iterable<V> values) {
		if (!allowModify) {
			throw new UnsupportedOperationException();
		}
		C c = absent(key);
		values.forEach(c::add);
	}

	@Override
	public void addAll(Map<K, ? extends Collection<V>> map) {
		if (!allowModify) {
			throw new UnsupportedOperationException();
		}
		map.forEach(this::addAll);
	}

	@Override
	public void addAll(AbstractMultiValue<K, V, C> value) {
		if (!allowModify) {
			throw new UnsupportedOperationException();
		}
		value.forEach((this::addAll));
	}

	@Override
	public void put(K key, V value) {
		if (!allowModify) {
			throw new UnsupportedOperationException();
		}
		putAll(key, Collections.singletonList(value));
	}

	@Override
	public void putAll(K key, Iterable<V> values) {
		if (!allowModify) {
			throw new UnsupportedOperationException();
		}
		C c = supplier.get();
		values.forEach(c::add);
		key = convert(key);
		map.put(key, c);
	}

	@Override
	public void putAll(Map<K, Collection<V>> map) {
		if (!allowModify) {
			throw new UnsupportedOperationException();
		}
		map.forEach(this::putAll);
	}

	@Override
	public void putAll(AbstractMultiValue<K, V, C> value) {
		if (!allowModify) {
			throw new UnsupportedOperationException();
		}
		value.forEach((this::putAll));
	}

	protected void from(MultiValue<K, V, ? extends Collection<V>> value) {
		from(value, vs -> {
			C c = supplier.get();
			c.addAll(vs);
			return c;
		});
	}

	protected <S extends Collection<V>> void from(MultiValue<K, V, S> value, Function<S, C> function) {
		value.forEach(((k, vs) -> {
			K rk = convert(k);
			C rv = function.apply(vs);
			map.put(rk, rv);
		}));
	}

	// endregion

	// region get

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public boolean isEmpty(K key) {
		return isEmpty() || !hasKey(key) || absent(key).isEmpty();
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public boolean hasKey(K key) {
		key = convert(key);
		return map.containsKey(key);
	}

	@Override
	public C get(K key) {
		if (!allowModify && !hasKey(key)) {
			return supplier.get();
		}
		return absent(key);
	}

	@Override
	public Iterator<V> iterator(K key) {
		return get(key).iterator();
	}

	@Override
	public V first(K key) {
		Collection<V> collection = get(key);
		if (CollectionUtils.isEmpty(collection)) {
			return null;
		}
		return collection.iterator().next();
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
	public Map<K, C> map() {
		Map<K, C> hashMap = new HashMap<>();
		this.map.forEach((k, vs) -> {
			C c = supplier.get();
			c.addAll(vs);
			hashMap.put(k, c);
		});
		return hashMap;
	}

	@Override
	public Set<Map.Entry<K, C>> entries() {
		return map.entrySet();
	}

	@Override
	public MultiValue<K, V, Collection<V>> unmodifiable() {
		return new UnmodifiableMultiValue<>(this);
	}

	// endregion

	// region remove
	@Override
	public void clear() {
		if (!allowModify) {
			throw new UnsupportedOperationException();
		}
		map.clear();
	}

	@Override
	public C remove(K key) {
		if (!allowModify) {
			throw new UnsupportedOperationException();
		}
		absent(key);
		return map.remove(key);
	}

	@Override
	public boolean remove(K key, V value) {
		if (!allowModify) {
			throw new UnsupportedOperationException();
		}
		if (!hasKey(key)) {
			return false;
		}
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
