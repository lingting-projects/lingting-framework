package live.lingting.framework.queue

import live.lingting.framework.retry.Retry.value
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

/**
 * @author lingting 2024-01-26 15:01
 */
internal class CircularQueueTest {
    @Test
    @Throws(InterruptedException::class)
    fun test() {
        val queue = CircularQueue<Int>()
        queue.add(1).addAll(mutableListOf(2, 3)).add(4)

        Assertions.assertEquals(1, queue.pool())
        Assertions.assertEquals(2, queue.pool())
        Assertions.assertEquals(3, queue.pool())
        Assertions.assertEquals(4, queue.pool())
        Assertions.assertEquals(1, queue.pool())
    }
}
