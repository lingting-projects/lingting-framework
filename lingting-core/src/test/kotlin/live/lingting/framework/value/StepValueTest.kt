package live.lingting.framework.value

import live.lingting.framework.value.step.DecimalStepValue
import live.lingting.framework.value.step.IteratorStepValue
import live.lingting.framework.value.step.LongStepValue
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.math.BigInteger

/**
 * @author lingting 2023-12-19 11:41
 */
internal class StepValueTest {
    fun assertNumber(step: StepValue<out Number?>) {
        Assertions.assertTrue(step.hasNext())
        Assertions.assertEquals(1, step.next()!!.toLong())
        Assertions.assertEquals(2, step.next()!!.toLong())
        Assertions.assertEquals(3, step.next()!!.toLong())
        Assertions.assertFalse(step.hasNext())
        Assertions.assertThrowsExactly(NoSuchElementException::class.java) { step.next() }
        val values = step.values()
        Assertions.assertEquals(3, values.size)
        Assertions.assertEquals(1, values[0]!!.toLong())
    }

    @Test
    fun testLong() {
        val step = LongStepValue(1, 3L, null)
        assertNumber(step)
        val copy = step.copy()
        assertNumber(copy)

        val max: StepValue<Long> = LongStepValue(5, null, 15L).start(5L)
        Assertions.assertEquals(10, max.next())
        Assertions.assertEquals(15, max.next())
        Assertions.assertFalse(max.hasNext())
    }

    @Test
    fun testDecimal() {
        val step = DecimalStepValue(BigDecimal.ONE, BigInteger.valueOf(3), null)
        assertNumber(step)
        val copy = step.copy()
        assertNumber(copy)

        val max = DecimalStepValue(BigDecimal.valueOf(5), null, BigDecimal.valueOf(15))
            .start(BigDecimal.valueOf(5))
        Assertions.assertEquals(10, max.next()!!.toLong())
        Assertions.assertEquals(15, max.next()!!.toLong())
        Assertions.assertFalse(max.hasNext())
    }

    @Test
    fun testIterator() {
        val list: List<Int> = ArrayList(mutableListOf(1, 2, 3))
        val step = IteratorStepValue<Int?>(list.iterator())
        assertNumber(step)
        val copy = step.copy()
        assertNumber(copy)
        val remove = IteratorStepValue<Int?>(list.iterator())
        val values = remove.values()
        Assertions.assertEquals(3, values.size)
        Assertions.assertThrowsExactly(IllegalStateException::class.java) { remove.remove() }
        Assertions.assertEquals(1, remove.next())
        Assertions.assertDoesNotThrow { remove.remove() }
        Assertions.assertEquals(BigInteger.ZERO, remove.index())
        Assertions.assertEquals(2, remove.next())
        Assertions.assertEquals(3, remove.next())
        Assertions.assertDoesNotThrow { remove.remove() }
        Assertions.assertFalse(remove.hasNext())
        Assertions.assertThrowsExactly(NoSuchElementException::class.java) { remove.next() }
        remove.reset()
        Assertions.assertEquals(2, remove.next())
        Assertions.assertFalse(remove.hasNext())
        Assertions.assertDoesNotThrow { remove.remove() }
        remove.reset()
        Assertions.assertFalse(remove.hasNext())
    }
}
