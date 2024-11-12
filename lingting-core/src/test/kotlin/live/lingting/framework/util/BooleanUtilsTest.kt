package live.lingting.framework.util;

import org.junit.jupiter.api.Test;

import static live.lingting.framework.util.BooleanUtils.isFalse;
import static live.lingting.framework.util.BooleanUtils.isTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author lingting 2024-01-26 17:22
 */
class BooleanUtilsTest {

	@Test
	void test() {
		assertTrue(isTrue("y"));
		assertFalse(isTrue("n"));
		assertTrue(isFalse("n"));
		assertFalse(isFalse("y"));
	}

}
