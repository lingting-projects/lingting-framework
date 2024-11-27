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
        assertFalse((array as Any).isEmpty())
        assertFalse((array).isEmpty())
        assertEquals(0, array.indexOf("1"))
        assertEquals(ArrayUtils.NOT_FOUNT, array.indexOf("a"))
        assertTrue(array.contains("1"))
        assertFalse(array.contains("c"))
        assertTrue(array.containsIgnoreCase("a"))
        assertFalse(array.containsIgnoreCase("c"))

        val array1 = arrayOf(1, 2, 3)
        val array2 = arrayOf(1, 2, 3, 4)
        val array3 = arrayOf(1, 2, 3)
        val array4 = arrayOf(1, 2, 3, 1, 2, 3, 4, 5)

        assertTrue(array1.isEquals(array3))
        assertFalse(array1.isEquals(array2))

        assertTrue(array1.isEquals(0, array2, 0, 3))
        assertFalse(array1.isEquals(0, array2, 0, 4))
        assertTrue(array1.isEquals(0, array3, 0, 4))
        assertTrue(array1.isEquals(0, array4, 0, 3))
        assertTrue(array2.isEquals(0, array4, 3, 4))
        assertTrue(array2.isEquals(array4.sub(3, 7)))
        assertFalse(array2.isEquals(array4.sub(3, 8)))
        assertTrue(array2.isEquals(0, array4.sub(3, 8), 0, array2.size))
    }
}
