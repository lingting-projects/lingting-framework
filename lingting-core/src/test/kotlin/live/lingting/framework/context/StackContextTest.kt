package live.lingting.framework.context

import live.lingting.framework.thread.Async
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

/**
 * @author lingting 2024-03-29 13:38
 */
class StackContextTest {

    companion object {
        val local = StackContext<Long?>()
    }

    @Test
    fun test() {
        val max = 1000
        val async = Async(200)
        for (i in 0 until max) {
            async.submit("stack-$i") {
                assertStack()
                assertStack()
                assertStack()
            }
        }

        async.await()
        assertEquals(max.toLong(), async.allCount())
    }

    fun assertStack() {
        val id = Thread.currentThread().threadId()
        assertNull(local.peek())
        local.push(id)
        assertEquals(id, local.peek())
        assertEquals(id, local.pop())
        assertNull(local.peek())
        assertNull(local.pop())
        local.push(null)
        assertNull(local.peek())
        assertNull(local.pop())
    }

}
