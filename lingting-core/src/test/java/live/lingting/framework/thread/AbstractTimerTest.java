package live.lingting.framework.thread;

import live.lingting.framework.context.ContextHolder;
import live.lingting.framework.util.ValueUtils;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author lingting 2023-12-20 21:53
 */
class AbstractTimerTest {

	@Test
	void test() throws InterruptedException {
		ContextHolder.start();
		AtomicInteger atomic = new AtomicInteger(0);

		AbstractTimer timer = new AbstractTimer() {

			@Override
			public Duration getTimeout() {
				// 设置执行间隔
				return Duration.ofMinutes(30);
			}

			@Override
			protected void process() throws Exception {
				atomic.set(atomic.get() + 1);
			}
		};

		timer.onApplicationStart();
		ValueUtils.await(atomic::get, v -> v > 0);
		assertEquals(1, atomic.get());
		timer.wake();
		ValueUtils.await(atomic::get, v -> v > 1);
		assertEquals(2, atomic.get());
		Thread thread = timer.threadValue.getValue();
		assertNotNull(thread);
		assertFalse(thread.isInterrupted());
		thread.interrupt();
		assertTrue(thread.isInterrupted());
	}

}
