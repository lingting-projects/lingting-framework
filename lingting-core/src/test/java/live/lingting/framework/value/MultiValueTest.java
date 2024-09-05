package live.lingting.framework.value;

import live.lingting.framework.value.multi.ListMultiValue;
import live.lingting.framework.value.multi.UnmodifiableMultiValue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author lingting 2024-09-05 20:32
 */
class MultiValueTest {

	@Test
	void list() {
		ListMultiValue<Object, Object> value = new ListMultiValue<>();
		assertTrue(value.isEmpty());
		value.add(1, 1);
		value.add(1, 2);
		value.add(1, 3);
		assertFalse(value.isEmpty());
		assertEquals(1, value.keys().size());
		assertEquals(3, value.get(1).size());
		assertArrayEquals(new Integer[] { 1, 2, 3 }, value.get(1).stream().sorted().toArray());
		assertEquals(1, value.first(1));
		UnmodifiableMultiValue<Object, Object> unmodifiable = value.unmodifiable();
		assertThrows(UnsupportedOperationException.class, () -> unmodifiable.add(1, 4));
	}

}
