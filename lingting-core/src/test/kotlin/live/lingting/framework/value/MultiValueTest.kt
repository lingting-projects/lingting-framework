package live.lingting.framework.value

import live.lingting.framework.value.multi.ListMultiValue
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * @author lingting 2024-09-05 20:32
 */
internal class MultiValueTest {
    @Test
    fun list() {
        val value = ListMultiValue<Any, Any>()
        assertTrue(value.isEmpty)
        assertNull(value.first(1))
        assertFalse(value.isEmpty)
        assertTrue(value.isEmpty(1))
        value.add(1, 1)
        assertFalse(value.isEmpty(1))
        value.clear()
        assertTrue(value.isEmpty(1))
        value.add(1, 1)
        value.add(1, 2)
        value.add(1, 3)
        assertFalse(value.isEmpty)
        assertEquals(1, value.keys().size)
        assertEquals(3, value.get(1).size)
        assertArrayEquals(arrayOf(1, 2, 3), value.get(1).stream().sorted().toArray())
        assertEquals(1, value.first(1))
        assertTrue(value.remove(1, 1))
        assertFalse(value.remove(2, 1))
        assertTrue(value.remove(2)!!.isEmpty())
        val unmodifiable = value.unmodifiable()
        assertUnmodifiable(unmodifiable)
    }

    fun assertUnmodifiable(unmodifiable: MultiValue<Any, Any, out Collection<Any>>) {
        assertThrows(UnsupportedOperationException::class.java) { unmodifiable.add(1, 4) }
        assertThrows(UnsupportedOperationException::class.java) { unmodifiable.clear() }
        assertThrows(UnsupportedOperationException::class.java) { unmodifiable.ifAbsent(2) }
        assertThrows(UnsupportedOperationException::class.java) { unmodifiable.remove(2) }
        assertThrows(UnsupportedOperationException::class.java) { unmodifiable.remove(1, 1) }
        assertDoesNotThrow<Collection<Any>> { unmodifiable.get(2) }
        assertDoesNotThrow<Any> { unmodifiable.first(1) }
        assertDoesNotThrow<Any> { unmodifiable.first(2) }
        assertDoesNotThrow<Any> { unmodifiable.isEmpty }
        assertDoesNotThrow<Boolean> { unmodifiable.isEmpty(1) }
        assertDoesNotThrow<Iterator<Any>> { unmodifiable.iterator(1) }
        assertDoesNotThrow<Iterator<Any>> { unmodifiable.iterator(2) }
    }
}
