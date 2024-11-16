package live.lingting.framework.value

import live.lingting.framework.value.cycle.IteratorCycleValue
import live.lingting.framework.value.cycle.StepCycleValue
import live.lingting.framework.value.step.LongStepValue
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrowsExactly
import org.junit.jupiter.api.Test

/**
 * @author lingting 2024-01-23 15:45
 */
internal class CycleValueTest {
    fun assertNumber(cycle: CycleValue<out Number?>) {
        assertEquals(1, cycle.next()!!.toLong())
        assertEquals(2, cycle.next()!!.toLong())
        assertEquals(3, cycle.next()!!.toLong())
        assertEquals(1, cycle.next()!!.toLong())
        assertEquals(2, cycle.next()!!.toLong())
        assertEquals(3, cycle.next()!!.toLong())
        assertEquals(1, cycle.next()!!.toLong())
        cycle.reset()
        assertEquals(1, cycle.next()!!.toLong())
        assertEquals(8, cycle.count().toLong())
    }

    @Test
    fun testStep() {
        val cycle = StepCycleValue(LongStepValue(1, 3, 99))
        assertNumber(cycle)
    }

    @Test
    fun testIterator() {
        val list: List<Int> = ArrayList(mutableListOf(1, 2, 3))
        val cycle = IteratorCycleValue<Int?>(list.iterator())
        assertNumber(cycle)
        cycle.reset()
        assertEquals(1, cycle.next())
        assertDoesNotThrow { cycle.remove() }
        assertEquals(2, cycle.next())
        assertEquals(3, cycle.next())
        assertDoesNotThrow { cycle.remove() }
        assertEquals(2, cycle.next())
        cycle.reset()
        assertThrowsExactly(IllegalStateException::class.java) { cycle.remove() }
        assertEquals(2, cycle.next())
        assertDoesNotThrow { cycle.remove() }
        assertThrowsExactly(NoSuchElementException::class.java) { cycle.next() }
    }
}
