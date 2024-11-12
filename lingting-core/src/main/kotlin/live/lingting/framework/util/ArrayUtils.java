package live.lingting.framework.util;

import java.lang.reflect.Array;
import java.util.Objects;
import java.util.function.BiPredicate;

/**
 * @author lingting
 */
@SuppressWarnings("java:S1168")
public final class ArrayUtils {

	public static final int NOT_FOUNT = -1;

	private ArrayUtils() {throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");}

	/**
	 * 数组是否为空
	 *
	 * @param obj 对象
	 * @return true表示为空, 如果对象不为数组, 返回false
	 */
	public static boolean isEmpty(Object obj) {
		if (obj == null) {
			return true;
		}
		if (!obj.getClass().isArray()) {
			return false;
		}

		int length = Array.getLength(obj);
		return length < 1;
	}

	public static <T> boolean isEmpty(T[] array) {
		return array == null || array.length == 0;
	}

	public static <T> int indexOf(T[] array, T val) {
		return indexOf(array, val, Objects::equals);
	}

	public static <T> int indexOf(T[] array, T val, BiPredicate<T, T> predicate) {
		if (!isEmpty(array)) {
			for (int i = 0; i < array.length; i++) {
				T t = array[i];
				if (predicate.test(t, val)) {
					return i;
				}
			}
		}
		return NOT_FOUNT;
	}

	public static <T> boolean contains(T[] array, T val) {
		return indexOf(array, val) > NOT_FOUNT;
	}

	public static boolean containsIgnoreCase(String[] array, String val) {
		return indexOf(array, val, (s, t) -> {
			if (Objects.equals(s, t)) {
				return true;
			}

			if (s == null || t == null) {
				return false;
			}

			return s.equalsIgnoreCase(t);
		}) > NOT_FOUNT;
	}

	public static <T> boolean isEquals(T[] array1, T[] array2) {
		boolean empty1 = isEmpty(array1);
		boolean empty2 = isEmpty(array2);

		if (empty1 || empty2) {
			return empty1 && empty2;
		}

		if (array1.length != array2.length) {
			return false;
		}

		for (int i = 0; i < array1.length; i++) {
			if (!Objects.equals(array1[i], array2[i])) {
				return false;
			}
		}
		return true;
	}

	public static <T> boolean isEquals(T[] array1, int array1Pos, T[] array2, int array2Pos, int len) {
		boolean empty1 = isEmpty(array1);
		boolean empty2 = isEmpty(array2);

		if (empty1 || empty2) {
			return empty1 && empty2;
		}

		for (int i = 0; i < len; i++) {
			int i1 = array1Pos + i;
			int i2 = array2Pos + i;

			// 是否越界
			boolean o1 = array1.length <= i1;
			boolean o2 = array2.length <= i2;

			if (o1 || o2) {
				// 如果同时越界, 则相等, 否则不等
				return o1 && o2;
			}

			T t1 = array1[i1];
			T t2 = array2[i2];
			if (!Objects.equals(t1, t2)) {
				return false;
			}
		}
		return true;

	}

	public static <T> T[] sub(T[] array, int start) {
		if (array == null) {
			return null;
		}
		return sub(array, start, array.length);
	}

	/**
	 * 截取数组
	 *
	 * @param array 数组
	 * @param start 左闭
	 * @param end   右开
	 */
	public static <T> T[] sub(T[] array, int start, int end) {
		if (array == null) {
			return null;
		}
		Class<?> type = array.getClass().getComponentType();

		if (array.length < 1) {
			return (T[]) Array.newInstance(type, 0);
		}

		int fromIndex = Math.max(0, start);
		int toIndex = Math.min(array.length + 1, end);
		T[] result = (T[]) Array.newInstance(type, toIndex - fromIndex);

		for (int i = fromIndex; i < toIndex; i++) {
			T t = array[i];
			result[i - fromIndex] = t;
		}

		return result;
	}

}
