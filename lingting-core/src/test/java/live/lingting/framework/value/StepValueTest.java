package live.lingting.framework.value;

import live.lingting.framework.value.step.IteratorStepValue;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author lingting 2023-12-19 11:41
 */
class StepValueTest {

	@Test
	void testMaxCount() {
		StepValue<Long> simple = StepValue.simple(1, 3L, null);
		assertEquals(0, simple.getCount());
		assertTrue(simple.hasNext());
		assertEquals(1, simple.next());
		assertTrue(simple.hasNext());
		assertEquals(2, simple.next());
		assertTrue(simple.hasNext());
		assertEquals(3, simple.next());
		assertFalse(simple.hasNext());
		assertThrowsExactly(NoSuchElementException.class, simple::next);
	}

	@Test
	void testMaxValue() {
		StepValue<Long> simple = StepValue.simple(5, null, 30L);
		assertTrue(simple.hasNext());
		assertEquals(5, simple.next());
		assertTrue(simple.hasNext());
		assertEquals(10, simple.next());
		assertTrue(simple.hasNext());
		assertEquals(15, simple.next());
		assertTrue(simple.hasNext());
		assertEquals(20, simple.next());
		assertTrue(simple.hasNext());
		assertEquals(25, simple.next());
		assertTrue(simple.hasNext());
		assertEquals(30, simple.next());
		assertFalse(simple.hasNext());
		assertThrowsExactly(NoSuchElementException.class, simple::next);
	}

	@Test
	void testCopy() {
		StepValue<Long> simple = StepValue.simple(5, null, 50L).start(40L).copy();
		assertTrue(simple.hasNext());
		assertEquals(45, simple.next());
		assertTrue(simple.hasNext());
		assertEquals(50, simple.next());
	}

	@Test
	void testStartValue() {
		StepValue<Long> simple = StepValue.simple(5, null, 50L).start(40L);
		assertTrue(simple.hasNext());
		assertEquals(45, simple.next());
		assertTrue(simple.hasNext());
		assertEquals(50, simple.next());
	}

	@Test
	void testValues() {
		StepValue<Long> simple = StepValue.simple(1, 3L, null);
		List<Long> values = simple.values();
		assertEquals(0, simple.getCount());
		assertTrue(simple.hasNext());
		assertEquals(3, values.size());
		assertEquals(1, values.get(0));
	}

	@Test
	void testIterator() {
		List<Integer> list = Arrays.asList(1, 2, 3);
		StepValue<Integer> step = new IteratorStepValue<>(list.iterator());
		assertEquals(1, step.next());
		assertEquals(2, step.next());
		step.reset();
		assertEquals(1, step.next());
		assertEquals(2, step.next());
		assertEquals(3, step.next());
		assertFalse(step.hasNext());
		assertThrowsExactly(NoSuchElementException.class, step::next);
		step.reset();
		assertTrue(step.hasNext());
	}

}
