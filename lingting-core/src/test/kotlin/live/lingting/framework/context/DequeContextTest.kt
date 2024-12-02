package live.lingting.framework.context

import java.util.ArrayDeque
import java.util.Deque
import java.util.function.Supplier
import live.lingting.framework.thread.Async
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

/**
 * @author lingting 2024-03-29 13:38
 */
class DequeContextTest {

    companion object {
        val local = DequeContext<Long?>(Supplier<Deque<Long?>> {
            val deque = ArrayDeque<Long?>()
            deque.push(0)
            deque
        })
    }

    @Test
    fun test() {
        val max = 1000
        val async = Async(200)
        for (i in 0 until max) {
            async.submit("stack-$i") {
                try {
                    assertContext()
                } finally {
                    local.remove()
                }
                try {
                    assertContext()
                } finally {
                    local.remove()
                }
                try {
                    assertContext()
                } finally {
                    local.remove()
                }
            }
        }

        async.await()
        assertEquals(max.toLong(), async.allCount())
    }

    fun assertContext() {
        val id = Thread.currentThread().threadId()
        assertEquals(0, local.peek())
        assertEquals(0, local.pop())
        assertNull(local.peek())
        local.push(id)
        assertEquals(id, local.peek())
        assertEquals(id, local.pop())
        assertNull(local.peek())
        assertNull(local.pop())
        local.push(1)
        local.push(2)
        local.push(3)
        assertEquals(3, local.peek())
        assertEquals(3, local.pop())
        assertEquals(2, local.pop())
        assertEquals(1, local.peek())
    }

}
