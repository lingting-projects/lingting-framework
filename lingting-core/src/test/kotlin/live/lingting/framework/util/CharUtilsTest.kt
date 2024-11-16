package live.lingting.framework.util

import live.lingting.framework.util.CharUtils.isLetter
import live.lingting.framework.util.CharUtils.isLowerLetter
import live.lingting.framework.util.CharUtils.isUpperLetter
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
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
