package live.lingting.framework.thread

import live.lingting.framework.thread.executor.PerThreadExecutor
import live.lingting.framework.thread.platform.PlatformThread
import live.lingting.framework.thread.virtual.VirtualThread
import live.lingting.framework.util.MdcUtils
import live.lingting.framework.util.ThreadUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import java.time.Duration
import kotlin.test.assertNotEquals

/**
 * @author lingting 2024-04-23 11:50
 */
class ThreadPoolTest {

    @Test
    fun testPool() {
        ThreadUtils.delegator = PlatformThread
        testMdc()
    }

    @Test
    fun testVirtual() {
        if (!VirtualThread.isSupport) {
            return
        }
        ThreadUtils.delegator = VirtualThread
        testMdc()
        val factory = VirtualThread.threadFactory()
        assertNotNull(factory)
        val executor = VirtualThread.find(PerThreadExecutor::class.java)
        assertNotNull(executor)
        assertEquals(0, executor?.threads?.size)
    }

    fun testMdc() {
        val instance = ThreadUtils.delegator
        val async = Async(instance)
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
