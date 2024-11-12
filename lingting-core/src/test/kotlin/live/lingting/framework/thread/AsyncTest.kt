package live.lingting.framework.thread;

import live.lingting.framework.time.StopWatch;
import live.lingting.framework.util.ThreadUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author lingting 2024-01-26 17:07
 */
class AsyncTest {

	private static final Logger log = org.slf4j.LoggerFactory.getLogger(AsyncTest.class);
	Executor executor;

	@Test
	void test() {
		executor = ThreadUtils.executor();
		doTest();
		executor = VirtualThread.executor();
		doTest();
	}

	void doTest() {
		int max = 10;

		StopWatch watch = new StopWatch();
		watch.start();

		Async async = new Async(executor);
		for (int i = 0; i < max; i++) {
			async.submit("Async-" + i, () -> LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(500)));
		}
		async.await();
		watch.stop();

		assertTrue(watch.timeMillis() > 500);
		assertEquals(0, async.notCompletedCount());
		assertEquals(max, async.allCount());
	}

	@Test
	void testLimit() {
		executor = ThreadUtils.executor();
		doTestLimit();
		executor = VirtualThread.executor();
		doTestLimit();
	}

	void doTestLimit() {
		long limit = 5;
		int max = 10;
		Async async = new Async(executor, limit);
		for (int i = 0; i < max; i++) {
			async.submit("Async-" + i, () -> LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(500)));
		}

		while (async.notCompletedCount() > 0) {
			long runningCount = async.runningCount();
			// 执行中数量必须小于等于线程数限制
			assertTrue(runningCount <= async.getLimit());
			LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(95));
		}
		assertEquals(max, async.allCount());
	}

	@Test
	void testMulti() {
		executor = ThreadUtils.executor();
		doTestMulti();
		executor = VirtualThread.executor();
		doTestMulti();
	}

	void doTestMulti() {
		int max = 100000;
		Async async = new Async(executor);
		for (int i = 0; i < max; i++) {
			async.submit("Async-" + i, () -> LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(1)));
		}
		async.await();

		assertEquals(0, async.notCompletedCount());
		assertEquals(max, async.allCount());
	}

}
