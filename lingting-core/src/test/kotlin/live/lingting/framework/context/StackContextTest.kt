package live.lingting.framework.context

import live.lingting.framework.thread.Async
import live.lingting.framework.util.Slf4jUtils.logger
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import kotlin.concurrent.atomics.AtomicLong
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.incrementAndFetch

/**
 * @author lingting 2024-03-29 13:38
 */
class StackContextTest {

    companion object {

        val local = StackContext<Long?>()

        val log = logger()

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
                    assertStack()
                    assertStack()
                    assertStack()
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
