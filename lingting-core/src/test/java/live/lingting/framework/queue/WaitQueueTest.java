package live.lingting.framework.queue;

import live.lingting.framework.util.ThreadUtils;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author lingting 2024-01-26 15:05
 */
class WaitQueueTest {

	@Test
	void test() {
		WaitQueue<Integer> queue = new WaitQueue<>();
		assertNull(queue.get());
		queue.add(1);
		assertEquals(1, queue.get());

		AtomicBoolean atomic = new AtomicBoolean();
		ThreadUtils.execute(() -> {
			assertEquals(2, queue.poll());
		});
		queue.add(2);

	}

}
