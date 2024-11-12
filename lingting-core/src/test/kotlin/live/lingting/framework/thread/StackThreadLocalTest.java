package live.lingting.framework.thread;


import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author lingting 2024-03-29 13:38
 */
class StackThreadLocalTest {

	static final StackThreadLocal<Long> local = new StackThreadLocal<>();
	private static final Logger log = org.slf4j.LoggerFactory.getLogger(StackThreadLocalTest.class);


	@Test
	void test() {
		int max = 1000;
		Async async = new Async();
		for (int i = 0; i < max; i++) {
			async.submit("stack-" + i, () -> {
				assertStack();
				assertStack();
				assertStack();
			});
		}

		async.await();
		assertEquals(max, async.allCount());
	}

	void assertStack() {
		long id = Thread.currentThread().threadId();
		assertNull(local.get());
		local.put(id);
		assertEquals(id, local.get());
		assertEquals(id, local.pop());
		assertNull(local.get());
		assertNull(local.pop());
		local.put(null);
		assertNull(local.get());
		assertNull(local.pop());
	}

}
