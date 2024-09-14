package live.lingting.framework.value;

import live.lingting.framework.value.multi.ListMultiValue;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
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
		assertNull(value.first(1));
		assertFalse(value.isEmpty());
		assertTrue(value.isEmpty(1));
		value.add(1, 1);
		assertFalse(value.isEmpty(1));
		value.clear();
		assertTrue(value.isEmpty(1));
		value.add(1, 1);
		value.add(1, 2);
		value.add(1, 3);
		assertFalse(value.isEmpty());
		assertEquals(1, value.keys().size());
		assertEquals(3, value.get(1).size());
		assertArrayEquals(new Integer[] { 1, 2, 3 }, value.get(1).stream().sorted().toArray());
		assertEquals(1, value.first(1));
		assertTrue(value.remove(1, 1));
		assertFalse(value.remove(2, 1));
		assertTrue(value.remove(2).isEmpty());
		MultiValue<Object, Object, Collection<Object>> unmodifiable = value.unmodifiable();
		assertUnmodifiable(unmodifiable);
	}

	void assertUnmodifiable(MultiValue<Object, Object, Collection<Object>> unmodifiable) {
		assertThrows(UnsupportedOperationException.class, () -> unmodifiable.add(1, 4));
		assertThrows(UnsupportedOperationException.class, () -> unmodifiable.clear());
		assertThrows(UnsupportedOperationException.class, () -> unmodifiable.ifAbsent(2));
		assertThrows(UnsupportedOperationException.class, () -> unmodifiable.remove(2));
		assertThrows(UnsupportedOperationException.class, () -> unmodifiable.remove(1, 1));
		assertDoesNotThrow(() -> unmodifiable.get(2));
		assertDoesNotThrow(() -> unmodifiable.first(1));
		assertDoesNotThrow(() -> unmodifiable.first(2));
		assertDoesNotThrow(() -> unmodifiable.isEmpty());
		assertDoesNotThrow(() -> unmodifiable.isEmpty(1));
		assertDoesNotThrow(() -> unmodifiable.iterator(1));
		assertDoesNotThrow(() -> unmodifiable.iterator(2));
	}

}
