package live.lingting.framework.thread

import live.lingting.framework.thread.executor.PerThreadExecutor
import live.lingting.framework.thread.platform.PlatformThread
import live.lingting.framework.thread.virtual.VirtualThread
import live.lingting.framework.util.MdcUtils
import live.lingting.framework.util.ThreadUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import java.time.Duration
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.TimeUnit

/**
 * @author lingting 2024-04-23 11:50
 */
class ThreadPoolTest {

    @Test
    fun test() {
        doTest(PlatformThread)
        doTest(VirtualThread)
    }

    fun doTest(delegator: ExecutorService) {
        ThreadUtils.delegator = delegator
        testMdc()
        testInvoke()
        if (!VirtualThread.isSupport || delegator != VirtualThread) {
            return
        }
        val factory = VirtualThread.threadFactory()
        assertNotNull(factory)
        val executor = VirtualThread.find(PerThreadExecutor::class.java)
        assertNotNull(executor)
        assertNotNull(executor?.threads?.size)
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

    fun testInvoke() {
        val callables = mutableListOf<Callable<Int?>>()
        for (i in 0..100) {
            callables.add(Callable { i })
        }

        val futures = ThreadUtils.invokeAll(callables)
        futures.forEachIndexed { i, f -> assertEquals(i, f.get()) }

        callables.clear()
        for (i in 0..100) {
            callables.add(Callable {
                Thread.sleep(i * 100L)
                i
            })
        }
        val i = ThreadUtils.invokeAny(callables, 10, TimeUnit.MILLISECONDS)
        assertEquals(0, i)
    }

}
