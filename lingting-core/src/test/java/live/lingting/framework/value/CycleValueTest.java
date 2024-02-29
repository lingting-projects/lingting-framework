package live.lingting.framework.value;

import live.lingting.framework.value.cycle.IteratorCycleValue;
import live.lingting.framework.value.cycle.StepCycleValue;
import live.lingting.framework.value.step.LongStepValue;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

/**
 * @author lingting 2024-01-23 15:45
 */
class CycleValueTest {

	void assertNumber(CycleValue<? extends Number> cycle) {
		assertEquals(1, cycle.next().longValue());
		assertEquals(2, cycle.next().longValue());
		assertEquals(3, cycle.next().longValue());
		assertEquals(1, cycle.next().longValue());
		assertEquals(2, cycle.next().longValue());
		assertEquals(3, cycle.next().longValue());
		assertEquals(1, cycle.next().longValue());
		cycle.reset();
		assertEquals(1, cycle.next().longValue());
		assertEquals(8, cycle.count().longValue());
	}

	@Test
	void testStep() {
		StepCycleValue<Long> cycle = new StepCycleValue<>(new LongStepValue(1, 3, 99));
		assertNumber(cycle);
	}

	@Test
	void testIterator() {
		List<Integer> list = new ArrayList<>(Arrays.asList(1, 2, 3));
		IteratorCycleValue<Integer> cycle = new IteratorCycleValue<>(list.iterator());
		assertNumber(cycle);
		cycle.reset();
		assertEquals(1, cycle.next());
		assertDoesNotThrow(cycle::remove);
		assertEquals(2, cycle.next());
		assertEquals(3, cycle.next());
		assertDoesNotThrow(cycle::remove);
		assertEquals(2, cycle.next());
		cycle.reset();
		assertThrowsExactly(IllegalStateException.class, cycle::remove);
		assertEquals(2, cycle.next());
		assertDoesNotThrow(cycle::remove);
		assertThrowsExactly(NoSuchElementException.class, cycle::next);
	}

}
