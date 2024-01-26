package live.lingting.framework.util;

import org.junit.jupiter.api.Test;

import static live.lingting.framework.util.ArrayUtils.NOT_FOUNT;
import static live.lingting.framework.util.ArrayUtils.contains;
import static live.lingting.framework.util.ArrayUtils.containsIgnoreCase;
import static live.lingting.framework.util.ArrayUtils.indexOf;
import static live.lingting.framework.util.ArrayUtils.isEmpty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author lingting 2024-01-26 17:19
 */
class ArrayUtilsTest {

	@Test
	void test() {
		String[] array = new String[] { "1", "A", "b" };
		assertFalse(isEmpty((Object) array));
		assertFalse(isEmpty(array));
		assertEquals(0, indexOf(array, "1"));
		assertEquals(NOT_FOUNT, indexOf(array, "a"));
		assertTrue(contains(array, "1"));
		assertFalse(contains(array, "c"));
		assertTrue(containsIgnoreCase(array, "a"));
		assertFalse(containsIgnoreCase(array, "c"));
	}

}
