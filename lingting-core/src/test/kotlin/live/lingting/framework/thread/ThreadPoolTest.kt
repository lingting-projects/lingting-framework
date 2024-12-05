package live.lingting.framework.thread

import java.time.Duration
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import live.lingting.framework.util.MdcUtils
import live.lingting.framework.util.ValueUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * @author lingting 2024-04-23 11:50
 */
internal class ThreadPoolTest {
    @Test
    fun testMdc() {
        val atomic = AtomicBoolean(false)
        val traceId = MdcUtils.setTraceId()

        val currentName = Thread.currentThread().name
        val executor = ThreadPoolExecutor(
            1, 1, 1, TimeUnit.MINUTES, LinkedBlockingQueue(1),
            CallerRunsPolicy()
        )
        ThreadPool.update(executor)

        ThreadPool.execute {
            Thread.sleep(Duration.ofSeconds(1).toMillis())
            assertEquals(traceId, MdcUtils.traceId)
            atomic.set(true)
        }
        ThreadPool.execute {
            Thread.sleep(Duration.ofSeconds(1).toMillis())
            assertEquals(traceId, MdcUtils.traceId)
            atomic.set(true)
        }
        ThreadPool.execute {
            assertEquals(currentName, Thread.currentThread().name)
            assertEquals(traceId, MdcUtils.traceId)
        }
        assertEquals(traceId, MdcUtils.traceId)
        ValueUtils.awaitTrue { atomic.get() }
    }
}
