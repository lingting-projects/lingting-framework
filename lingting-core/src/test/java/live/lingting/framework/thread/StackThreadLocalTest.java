package live.lingting.framework.thread;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author lingting 2024-03-29 13:38
 */
class StackThreadLocalTest {

	static final StackThreadLocal<Long> local = new StackThreadLocal<>();

	@SneakyThrows
	@Test
	void test() {
		Async async = new Async();

		for (int i = 0; i < 1000; i++) {
			async.submit("stack-" + i, () -> {
				assertStack();
				assertStack();
				assertStack();
			});
		}

		async.await();
	}

	void assertStack() {
		long id = Thread.currentThread().getId();
		assertNull(local.get());
		local.put(id);
		assertEquals(id, local.get());
		assertEquals(id, local.pop());
		assertNull(local.get());
		assertNull(local.pop());
	}

}
