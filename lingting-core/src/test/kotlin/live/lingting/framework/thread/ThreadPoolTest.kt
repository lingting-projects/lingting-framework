package live.lingting.framework.thread

import java.time.Duration
import kotlin.test.assertNotEquals
import live.lingting.framework.util.MdcUtils
import live.lingting.framework.util.ThreadUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * @author lingting 2024-04-23 11:50
 */
class ThreadPoolTest {
    @Test
    fun testPool() {
        ThreadUtils.instance = ThreadPool.instance
        testMdc()
    }

    @Test
    fun testVirtual() {
        ThreadUtils.instance = VirtualThread.instance
        testMdc()
    }

    fun testMdc() {
        val instance = ThreadUtils.instance
        val async = Async(instance.executor())
        val traceId = MdcUtils.setTraceId()
        val currentName = Thread.currentThread().name

        async.submit {
            Thread.sleep(Duration.ofSeconds(1).toMillis())
            assertEquals(traceId, MdcUtils.traceId)
        }

        async.submit {
            Thread.sleep(Duration.ofSeconds(1).toMillis())
            assertEquals(traceId, MdcUtils.traceId)
        }
        async.submit {
            assertNotEquals(currentName, Thread.currentThread().name)
            assertEquals(traceId, MdcUtils.traceId)
        }
        assertEquals(traceId, MdcUtils.traceId)
        async.await()
    }

}
