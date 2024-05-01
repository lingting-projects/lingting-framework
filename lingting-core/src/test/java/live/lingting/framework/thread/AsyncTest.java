package live.lingting.framework.thread;

import live.lingting.framework.StopWatch;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.LockSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author lingting 2024-01-26 17:07
 */
class AsyncTest {

	@Test
	void test() throws InterruptedException {
		StopWatch watch = new StopWatch();
		watch.start();

		AtomicLong atomic = new AtomicLong(0);
		Async async = new Async();

		for (int i = 0; i < 10; i++) {
			async.submit("Async-" + i, () -> {
				atomic.incrementAndGet();
				LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(500));
			});
		}

		async.await();
		watch.stop();

		assertTrue(watch.timeMillis() > 500);
		assertEquals(0, async.count());
		assertEquals(10, atomic.get());
	}

}
