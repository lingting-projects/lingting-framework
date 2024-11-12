package live.lingting.framework.thread

import live.lingting.framework.util.MdcUtils
import live.lingting.framework.util.ValueUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.Duration
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @author lingting 2024-04-23 11:50
 */
internal class ThreadPoolTest {
    @Test
    fun testMdc() {
        val atomic = AtomicBoolean(false)
        val traceId = MdcUtils.fillTraceId()

        val currentName = Thread.currentThread().name
        val executor = ThreadPoolExecutor(
            1, 1, 1, TimeUnit.MINUTES, LinkedBlockingQueue(1),
            CallerRunsPolicy()
        )
        ThreadPool.update(executor)

        ThreadPool.execute {
            Thread.sleep(Duration.ofSeconds(1).toMillis())
            assertEquals(traceId, MdcUtils.getTraceId())
            atomic.set(true)
        }
        ThreadPool.execute {
            Thread.sleep(Duration.ofSeconds(1).toMillis())
            assertEquals(traceId, MdcUtils.getTraceId())
            atomic.set(true)
        }
        ThreadPool.execute {
            Assertions.assertEquals(currentName, Thread.currentThread().name)
            assertEquals(traceId, MdcUtils.getTraceId())
        }
        assertEquals(traceId, MdcUtils.getTraceId())
        ValueUtils.awaitTrue { atomic.get() }
    }
}
