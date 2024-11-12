package live.lingting.framework.queue

import live.lingting.framework.util.ThreadUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

/**
 * @author lingting 2024-01-26 15:05
 */
internal class WaitQueueTest {
    @Test
    fun test() {
        val queue = WaitQueue<Int>()
        Assertions.assertNull(queue.get())
        queue.add(1)
        Assertions.assertEquals(1, queue.get())

        ThreadUtils.execute { Assertions.assertEquals(2, queue.poll()) }
        queue.add(2)
    }
}
