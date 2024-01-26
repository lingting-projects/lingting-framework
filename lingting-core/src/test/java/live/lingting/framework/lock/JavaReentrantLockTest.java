package live.lingting.framework.lock;

import live.lingting.framework.thread.Async;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author lingting 2024-01-26 11:25
 */
class JavaReentrantLockTest {

	@Test
	void test() throws InterruptedException {
		JavaReentrantLock lock = new JavaReentrantLock();

		AtomicInteger value = new AtomicInteger();
		int count = 2;
		Async async = new Async();

		for (int i = 0; i < count; i++) {
			async.submit("async-" + i, () -> {

				for (int j = 0; j < count; j++) {
					lock.run(() -> {
						value.set(value.get() + 1);
					});
				}

			});
		}

		async.await();
		assertEquals(count * count, value.get());
	}

}
