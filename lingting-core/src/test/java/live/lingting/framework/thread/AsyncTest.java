package live.lingting.framework.thread;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author lingting 2024-01-26 17:07
 */
class AsyncTest {

	@Test
	void test() throws InterruptedException {
		AtomicLong atomic = new AtomicLong(0);
		Async async = new Async();

		for (int i = 0; i < 10; i++) {
			async.submit("Async-" + i, atomic::incrementAndGet);
		}

		async.await();
		assertEquals(0, async.count());
		assertEquals(10, atomic.get());
	}

}
