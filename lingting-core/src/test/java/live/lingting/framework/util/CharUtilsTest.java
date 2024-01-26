package live.lingting.framework.util;

import org.junit.jupiter.api.Test;

import static live.lingting.framework.util.CharUtils.isLetter;
import static live.lingting.framework.util.CharUtils.isLowerLetter;
import static live.lingting.framework.util.CharUtils.isUpperLetter;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author lingting 2024-01-26 17:31
 */
class CharUtilsTest {

	@Test
	void test() {
		assertTrue(isLowerLetter('a'));
		assertFalse(isLowerLetter('A'));
		assertTrue(isUpperLetter('A'));
		assertFalse(isUpperLetter('a'));
		assertTrue(isLetter('a'));
		assertFalse(isLetter('1'));
	}

}
