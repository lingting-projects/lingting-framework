package live.lingting.framework.util;

import lombok.experimental.UtilityClass;

import java.lang.reflect.Array;
import java.util.Objects;
import java.util.function.BiPredicate;

/**
 * @author lingting
 */
@UtilityClass
public class ArrayUtils {

	public static final int NOT_FOUNT = -1;

	/**
	 * 数组是否为空
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

}
