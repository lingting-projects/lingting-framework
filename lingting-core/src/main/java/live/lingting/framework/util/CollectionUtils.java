package live.lingting.framework.util;

import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author lingting
 */
@UtilityClass
@SuppressWarnings("unchecked")
public class CollectionUtils {

	@SafeVarargs
	public static <T> List<T> toList(T... ts) {
		final ArrayList<T> list = new ArrayList<>();
		Collections.addAll(list, ts);
		return list;
	}

	@SafeVarargs
	public static <T> Set<T> toSet(T... ts) {
		HashSet<T> set = new HashSet<>();
		Collections.addAll(set, ts);
		return set;
	}

	public static boolean isEmpty(Collection<?> collection) {
		return collection == null || collection.isEmpty();
	}

	public static boolean isEmpty(Map<?, ?> map) {
		return map == null || map.isEmpty();
	}

	/**
	 * 是否是否可以存放多个数据
	 */
	public static boolean isMulti(Object obj) {
		if (obj == null) {
			return false;
		}
		return obj instanceof Iterable || obj instanceof Iterator || obj.getClass().isArray();
	}

	/**
	 * 复数对应转为 List
	 */
	public static List<Object> multiToList(Object obj) {
		if (obj == null) {
			return new ArrayList<>();
		}

		if (!isMulti(obj)) {
			return toList(obj);
		}
		else if (obj instanceof List<?>) {
			return (List<Object>) obj;
		}
		else if (obj instanceof Collection<?> collection) {
			return new ArrayList<>(collection);
		}

		List<Object> list = new ArrayList<>();

		if (obj.getClass().isArray()) {
			Collections.addAll(list, (Object[]) obj);
		}
		else if (obj instanceof Iterator) {
			((Iterator<?>) obj).forEachRemaining(list::add);
		}
		else if (obj instanceof Iterable) {
			((Iterable<?>) obj).forEach(list::add);
		}

		return list;
	}

	/**
	 * 提取集合中指定数量的元素,
	 * @param number 提取元素数量, 不足则有多少提取多少
	 */
	public static <D> List<D> extract(Collection<D> collection, int number) {
		return extract(collection.iterator(), number);
	}

	public static <D> List<D> extract(Iterator<D> iterator, int number) {
		List<D> list = new ArrayList<>(number);
		while (iterator.hasNext()) {
			list.add(iterator.next());
			if (list.size() == number) {
				break;
			}
		}
		return list;
	}

	/**
	 * 分割为多个小list, 每个list最多拥有 size个元素
	 * @param collection 原始数据
	 * @param size 单个list最多元素数量
	 * @return java.util.List<java.util.List < D>>
	 */
	public static <D> List<List<D>> split(Collection<D> collection, int size) {
		return split(collection.iterator(), size);
	}

	public static <D> List<List<D>> split(Iterator<D> iterator, int size) {
		List<List<D>> list = new ArrayList<>();

		List<D> items = new ArrayList<>(size);

		while (iterator.hasNext()) {
			D next = iterator.next();
			items.add(next);

			if (items.size() == size) {
				list.add(items);
				items = new ArrayList<>(size);
			}
		}

		if (!CollectionUtils.isEmpty(items)) {
			list.add(items);
		}

		return list;
	}

	public static <K, V> Map<K, V> toMap(Collection<K> keys, Collection<V> values) {
		Map<K, V> map = new HashMap<>();

		if (keys == null || keys.isEmpty()) {
			return map;
		}

		Iterator<K> keyIterator = keys.iterator();
		Iterator<V> valueIterator = values.iterator();

		while (keyIterator.hasNext() && valueIterator.hasNext()) {
			K key = keyIterator.next();
			V value = valueIterator.next();
			if (key != null && value != null) {
				map.put(key, value);
			}
		}

		return map;
	}

}
