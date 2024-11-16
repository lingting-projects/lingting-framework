package live.lingting.framework.value

import java.math.BigDecimal
import java.math.BigInteger
import live.lingting.framework.value.step.DecimalStepValue
import live.lingting.framework.value.step.IteratorStepValue
import live.lingting.framework.value.step.LongStepValue
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrowsExactly
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * @author lingting 2023-12-19 11:41
 */
internal class StepValueTest {
    fun assertNumber(step: StepValue<out Number>) {
        assertTrue(step.hasNext())
        assertEquals(1, step.next().toLong())
        assertEquals(2, step.next().toLong())
        assertEquals(3, step.next().toLong())
        assertFalse(step.hasNext())
        assertThrowsExactly(NoSuchElementException::class.java) { step.next() }
        val values = step.values()
        assertEquals(3, values.size)
        assertEquals(1, values[0].toLong())
    }

    @Test
    fun testLong() {
        val step = LongStepValue(1, 3L, null)
        assertNumber(step)
        val copy = step.copy()
        assertNumber(copy)

        val max: StepValue<Long> = LongStepValue(5, null, 15L).start(5L)
        assertEquals(10, max.next())
        assertEquals(15, max.next())
        assertFalse(max.hasNext())
    }

    @Test
    fun testDecimal() {
        val step = DecimalStepValue(BigDecimal.ONE, BigInteger.valueOf(3), null)
        assertNumber(step)
        val copy = step.copy()
        assertNumber(copy)

        val max = DecimalStepValue(BigDecimal.valueOf(5), null, BigDecimal.valueOf(15))
            .start(BigDecimal.valueOf(5))
        assertEquals(10, max.next().toLong())
        assertEquals(15, max.next().toLong())
        assertFalse(max.hasNext())
    }

    @Test
    fun testIterator() {
        val list: List<Int> = ArrayList(mutableListOf(1, 2, 3))
        val step = IteratorStepValue<Int>(list.iterator())
        assertNumber(step)
        val copy = step.copy()
        assertNumber(copy)
        val remove = IteratorStepValue<Int>(list.iterator())
        val values = remove.values()
        assertEquals(3, values.size)
        assertThrowsExactly(IllegalStateException::class.java) { remove.remove() }
        assertEquals(1, remove.next())
        assertDoesNotThrow { remove.remove() }
        assertEquals(BigInteger.ZERO, remove.index())
        assertEquals(2, remove.next())
        assertEquals(3, remove.next())
        assertDoesNotThrow { remove.remove() }
        assertFalse(remove.hasNext())
        assertThrowsExactly(NoSuchElementException::class.java) { remove.next() }
        remove.reset()
        assertEquals(2, remove.next())
        assertFalse(remove.hasNext())
        assertDoesNotThrow { remove.remove() }
        remove.reset()
        assertFalse(remove.hasNext())
    }
}
