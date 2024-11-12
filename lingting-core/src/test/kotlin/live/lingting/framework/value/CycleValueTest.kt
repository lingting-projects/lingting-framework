package live.lingting.framework.value

import live.lingting.framework.value.cycle.IteratorCycleValue
import live.lingting.framework.value.cycle.StepCycleValue
import live.lingting.framework.value.step.LongStepValue
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

/**
 * @author lingting 2024-01-23 15:45
 */
internal class CycleValueTest {
    fun assertNumber(cycle: CycleValue<out Number?>) {
        Assertions.assertEquals(1, cycle.next()!!.toLong())
        Assertions.assertEquals(2, cycle.next()!!.toLong())
        Assertions.assertEquals(3, cycle.next()!!.toLong())
        Assertions.assertEquals(1, cycle.next()!!.toLong())
        Assertions.assertEquals(2, cycle.next()!!.toLong())
        Assertions.assertEquals(3, cycle.next()!!.toLong())
        Assertions.assertEquals(1, cycle.next()!!.toLong())
        cycle.reset()
        Assertions.assertEquals(1, cycle.next()!!.toLong())
        Assertions.assertEquals(8, cycle.count().toLong())
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
        Assertions.assertEquals(1, cycle.next())
        Assertions.assertDoesNotThrow { cycle.remove() }
        Assertions.assertEquals(2, cycle.next())
        Assertions.assertEquals(3, cycle.next())
        Assertions.assertDoesNotThrow { cycle.remove() }
        Assertions.assertEquals(2, cycle.next())
        cycle.reset()
        Assertions.assertThrowsExactly(IllegalStateException::class.java) { cycle.remove() }
        Assertions.assertEquals(2, cycle.next())
        Assertions.assertDoesNotThrow { cycle.remove() }
        Assertions.assertThrowsExactly(NoSuchElementException::class.java) { cycle.next() }
    }
}
