package live.lingting.framework.value

import live.lingting.framework.value.multi.ListMultiValue
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

/**
 * @author lingting 2024-09-05 20:32
 */
internal class MultiValueTest {
    @Test
    fun list() {
        val value = ListMultiValue<Any, Any>()
        Assertions.assertTrue(value.isEmpty)
        Assertions.assertNull(value.first(1))
        Assertions.assertFalse(value.isEmpty)
        Assertions.assertTrue(value.isEmpty(1))
        value.add(1, 1)
        Assertions.assertFalse(value.isEmpty(1))
        value.clear()
        Assertions.assertTrue(value.isEmpty(1))
        value.add(1, 1)
        value.add(1, 2)
        value.add(1, 3)
        Assertions.assertFalse(value.isEmpty)
        Assertions.assertEquals(1, value.keys().size)
        Assertions.assertEquals(3, value.get(1)!!.size)
        Assertions.assertArrayEquals(arrayOf(1, 2, 3), value.get(1)!!.stream().sorted().toArray())
        Assertions.assertEquals(1, value.first(1))
        Assertions.assertTrue(value.remove(1, 1))
        Assertions.assertFalse(value.remove(2, 1))
        Assertions.assertTrue(value.remove(2)!!.isEmpty())
        val unmodifiable = value.unmodifiable()
        assertUnmodifiable(unmodifiable)
    }

    fun assertUnmodifiable(unmodifiable: MultiValue<Any, Any, Collection<Any>>) {
        Assertions.assertThrows(UnsupportedOperationException::class.java) { unmodifiable.add(1, 4) }
        Assertions.assertThrows(UnsupportedOperationException::class.java) { unmodifiable.clear() }
        Assertions.assertThrows(UnsupportedOperationException::class.java) { unmodifiable.ifAbsent(2) }
        Assertions.assertThrows(UnsupportedOperationException::class.java) { unmodifiable.remove(2) }
        Assertions.assertThrows(UnsupportedOperationException::class.java) { unmodifiable.remove(1, 1) }
        Assertions.assertDoesNotThrow<Collection<Any>> { unmodifiable.get(2) }
        Assertions.assertDoesNotThrow<Any?> { unmodifiable.first(1) }
        Assertions.assertDoesNotThrow<Any?> { unmodifiable.first(2) }
        Assertions.assertDoesNotThrow<Any> { unmodifiable.isEmpty }
        Assertions.assertDoesNotThrow<Boolean> { unmodifiable.isEmpty(1) }
        Assertions.assertDoesNotThrow<Iterator<Any>> { unmodifiable.iterator(1) }
        Assertions.assertDoesNotThrow<Iterator<Any>> { unmodifiable.iterator(2) }
    }
}
