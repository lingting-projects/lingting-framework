package live.lingting.framework.queue;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author lingting 2024-01-26 15:01
 */
class CircularQueueTest {

	@Test
	void test() throws InterruptedException {
		CircularQueue<Integer> queue = new CircularQueue<>();
		queue.add(1).addAll(Arrays.asList(2, 3)).add(4);

		assertEquals(1, queue.pool());
		assertEquals(2, queue.pool());
		assertEquals(3, queue.pool());
		assertEquals(4, queue.pool());
		assertEquals(1, queue.pool());
	}

}
