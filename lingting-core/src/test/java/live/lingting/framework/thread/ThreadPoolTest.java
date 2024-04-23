package live.lingting.framework.thread;

import live.lingting.framework.util.MdcUtils;
import live.lingting.framework.util.ValueUtils;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author lingting 2024-04-23 11:50
 */
@SuppressWarnings("java:S2925")
class ThreadPoolTest {

	@Test
	void testMdc() {
		AtomicBoolean atomic = new AtomicBoolean(false);
		String traceId = MdcUtils.fillTraceId();

		String currentName = Thread.currentThread().getName();
		ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 1, TimeUnit.MINUTES, new LinkedBlockingQueue<>(1),
				new ThreadPoolExecutor.CallerRunsPolicy());
		ThreadPool instance = ThreadPool.instance();
		ThreadPool.update(executor);

		instance.execute(() -> {
			Thread.sleep(Duration.ofSeconds(1).toMillis());
			assertEquals(traceId, MdcUtils.getTraceId());
			atomic.set(true);
		});
		instance.execute(() -> {
			Thread.sleep(Duration.ofSeconds(1).toMillis());
			assertEquals(traceId, MdcUtils.getTraceId());
			atomic.set(true);
		});
		instance.execute(() -> {
			assertEquals(currentName, Thread.currentThread().getName());
			assertEquals(traceId, MdcUtils.getTraceId());
		});
		assertEquals(traceId, MdcUtils.getTraceId());
		ValueUtils.awaitTrue(atomic::get);
	}

}
