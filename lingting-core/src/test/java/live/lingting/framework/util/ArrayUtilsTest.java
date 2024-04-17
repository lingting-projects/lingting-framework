package live.lingting.framework.util;

import org.junit.jupiter.api.Test;

import static live.lingting.framework.util.ArrayUtils.NOT_FOUNT;
import static live.lingting.framework.util.ArrayUtils.contains;
import static live.lingting.framework.util.ArrayUtils.containsIgnoreCase;
import static live.lingting.framework.util.ArrayUtils.indexOf;
import static live.lingting.framework.util.ArrayUtils.isEmpty;
import static live.lingting.framework.util.ArrayUtils.isEquals;
import static live.lingting.framework.util.ArrayUtils.sub;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author lingting 2024-01-26 17:19
 */
class ArrayUtilsTest {

	@Test
	void test() {
		assertWrap();
	}

	void assertWrap() {
		String[] array = { "1", "A", "b" };
		assertFalse(isEmpty((Object) array));
		assertFalse(isEmpty(array));
		assertEquals(0, indexOf(array, "1"));
		assertEquals(NOT_FOUNT, indexOf(array, "a"));
		assertTrue(contains(array, "1"));
		assertFalse(contains(array, "c"));
		assertTrue(containsIgnoreCase(array, "a"));
		assertFalse(containsIgnoreCase(array, "c"));

		Integer[] array1 = { 1, 2, 3 };
		Integer[] array2 = { 1, 2, 3, 4 };
		Integer[] array3 = { 1, 2, 3 };
		Integer[] array4 = { 1, 2, 3, 1, 2, 3, 4, 5 };

		assertTrue(isEquals(array1, array3));
		assertFalse(isEquals(array1, array2));

		assertTrue(isEquals(array1, 0, array2, 0, 3));
		assertFalse(isEquals(array1, 0, array2, 0, 4));
		assertTrue(isEquals(array1, 0, array3, 0, 4));
		assertTrue(isEquals(array1, 0, array4, 0, 3));
		assertTrue(isEquals(array2, 0, array4, 3, 4));
		assertTrue(isEquals(array2, sub(array4, 3, 7)));
		assertFalse(isEquals(array2, sub(array4, 3, 8)));
		assertTrue(isEquals(array2, 0, sub(array4, 3, 8), 0, array2.length));
	}

}
