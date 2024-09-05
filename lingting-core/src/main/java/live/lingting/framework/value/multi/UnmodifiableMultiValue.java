package live.lingting.framework.value.multi;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * @author lingting 2024-09-05 21:07
 */
public class UnmodifiableMultiValue<K, V> extends AbstractMultiValue<K, V, Collection<V>> {

	public UnmodifiableMultiValue(AbstractMultiValue<K, V, ?> value) {
		super(null);
		value.forEach(((k, vs) -> map.put(k, Collections.unmodifiableCollection(vs))));
	}

	@Override
	protected Collection<V> absent(K key) {
		if (map.containsKey(key)) {
			return map.get(key);
		}
		throw new UnsupportedOperationException();
	}

	@Override
	public void add(K key, V value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addAll(K key, Collection<V> values) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addAll(K key, Iterable<V> values) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addAll(Map<K, Collection<V>> map) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addAll(AbstractMultiValue<K, V, Collection<V>> value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void put(K key, V value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void putAll(K key, Iterable<V> values) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void putAll(Map<K, Collection<V>> map) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void putAll(AbstractMultiValue<K, V, Collection<V>> value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<V> get(K key) {
		if (!hasKey(key)) {
			return Collections.emptyList();
		}
		return super.get(key);
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<V> remove(K key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean remove(K key, V value) {
		throw new UnsupportedOperationException();
	}

}
