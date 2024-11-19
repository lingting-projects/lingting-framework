package live.lingting.framework.queue

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * @author lingting 2024-01-26 15:01
 */
internal class CircularQueueTest {
    @Test

    fun test() {
        val queue = CircularQueue<Int>()
        queue.add(1).addAll(mutableListOf(2, 3)).add(4)

        assertEquals(1, queue.pool())
        assertEquals(2, queue.pool())
        assertEquals(3, queue.pool())
        assertEquals(4, queue.pool())
        assertEquals(1, queue.pool())
    }
}
