package live.lingting.framework.value;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author lingting 2024-01-23 15:45
 */
class CycleValueTest {

	@Test
	void testIterator() {
		List<Integer> list = Arrays.asList(1, 2, 3);
		CycleValue<Integer> cycle = CycleValue.iterator(list.iterator());
		assertEquals(1, cycle.next());
		assertEquals(2, cycle.next());
		assertEquals(3, cycle.next());
		assertEquals(1, cycle.next());
		assertEquals(2, cycle.next());
		assertEquals(3, cycle.next());
		assertEquals(1, cycle.next());
	}

}
