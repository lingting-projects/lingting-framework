package live.lingting.framework.context

import live.lingting.framework.context.StackContextTest.Companion.log
import live.lingting.framework.thread.Async
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.util.ArrayDeque
import java.util.Deque
import java.util.function.Supplier
import kotlin.concurrent.atomics.AtomicLong
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.incrementAndFetch

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

    @OptIn(ExperimentalAtomicApi::class)
    @Test
    fun test() {
        val max = 1000
        val async = Async(200)
        val err = AtomicLong(0L)
        for (i in 0 until max) {
            async.submit("stack-$i") {
                try {
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
                } catch (e: Throwable) {
                    log.error("异常: ", e)
                    err.incrementAndFetch()
                } finally {
                    local.remove()
                }
            }
        }

        async.await()
        assertEquals(0, err.load())
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
