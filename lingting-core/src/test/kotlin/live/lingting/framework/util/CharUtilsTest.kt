package live.lingting.framework.util

import org.junit.jupiter.api.Test

/**
 * @author lingting 2024-01-26 17:31
 */
internal class CharUtilsTest {
    @Test
    fun test() {
        assertTrue(isLowerLetter('a'))
        assertFalse(isLowerLetter('A'))
        assertTrue(isUpperLetter('A'))
        assertFalse(isUpperLetter('a'))
        assertTrue(isLetter('a'))
        assertFalse(isLetter('1'))
    }
}
