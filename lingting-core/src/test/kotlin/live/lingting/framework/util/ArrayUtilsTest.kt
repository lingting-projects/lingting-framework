package live.lingting.framework.util

import live.lingting.framework.util.ArrayUtils.contains
import live.lingting.framework.util.ArrayUtils.containsIgnoreCase
import live.lingting.framework.util.ArrayUtils.indexOf
import live.lingting.framework.util.ArrayUtils.isEmpty
import live.lingting.framework.util.ArrayUtils.isEquals
import live.lingting.framework.util.ArrayUtils.sub
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * @author lingting 2024-01-26 17:19
 */
internal class ArrayUtilsTest {
    @Test
    fun test() {
        assertWrap()
    }

    fun assertWrap() {
        val array = arrayOf("1", "A", "b")
        assertFalse(isEmpty(array as Any))
        assertFalse(isEmpty(array))
        assertEquals(0, indexOf(array, "1"))
        assertEquals(ArrayUtils.NOT_FOUNT, indexOf(array, "a"))
        assertTrue(contains(array, "1"))
        assertFalse(contains(array, "c"))
        assertTrue(containsIgnoreCase(array, "a"))
        assertFalse(containsIgnoreCase(array, "c"))

        val array1 = arrayOf(1, 2, 3)
        val array2 = arrayOf(1, 2, 3, 4)
        val array3 = arrayOf(1, 2, 3)
        val array4 = arrayOf(1, 2, 3, 1, 2, 3, 4, 5)

        assertTrue(isEquals(array1, array3))
        assertFalse(isEquals(array1, array2))

        assertTrue(isEquals(array1, 0, array2, 0, 3))
        assertFalse(isEquals(array1, 0, array2, 0, 4))
        assertTrue(isEquals(array1, 0, array3, 0, 4))
        assertTrue(isEquals(array1, 0, array4, 0, 3))
        assertTrue(isEquals(array2, 0, array4, 3, 4))
        assertTrue(isEquals(array2, sub(array4, 3, 7)))
        assertFalse(isEquals(array2, sub(array4, 3, 8)))
        assertTrue(isEquals(array2, 0, sub(array4, 3, 8), 0, array2.size))
    }
}
