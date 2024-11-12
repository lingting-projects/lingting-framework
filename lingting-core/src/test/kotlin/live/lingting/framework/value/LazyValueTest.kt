package live.lingting.framework.value;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author lingting 2024-09-28 15:33
 */
class LazyValueTest {

	@Test
	void test() {
		LazyValue<String> lazyValue = new LazyValue<>(() -> "test");
		assertNull(lazyValue.t);
		assertTrue(lazyValue.isFirst());
		assertEquals("test", lazyValue.get());
		assertEquals("test", lazyValue.t);
		assertFalse(lazyValue.isFirst());
	}

}
