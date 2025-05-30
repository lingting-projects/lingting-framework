package live.lingting.framework.thread

import live.lingting.framework.thread.platform.PlatformThread
import live.lingting.framework.thread.virtual.VirtualThread
import live.lingting.framework.time.StopWatch
import live.lingting.framework.util.Slf4jUtils.logger
import live.lingting.framework.util.ThreadUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.LockSupport

/**
 * @author lingting 2024-01-26 17:07
 */
class AsyncTest {

    companion object {

        private val log = logger()

    }

    var executor: Executor? = null

    @Test
    fun test() {
        executor = PlatformThread
        doTest()
        executor = VirtualThread
        doTest()
    }

    fun doTest() {
        val max = 10

        val watch = StopWatch()
        watch.start()

        val async = Async(executor!!)
        for (i in 0 until max) {
            async.submit("Async-$i") { LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(500)) }
        }
        async.await()
        watch.stop()

        assertTrue(watch.timeMillis() >= 500)
        assertEquals(0, async.notCompletedCount())
        assertEquals(max.toLong(), async.allCount())
    }

    @Test
    fun testLimit() {
        executor = ThreadUtils
        doTestLimit()
        executor = VirtualThread
        doTestLimit()
    }

    fun doTestLimit() {
        val limit: Long = 5
        val max = 10
        val async = Async(executor!!, limit)
        for (i in 0 until max) {
            async.submit("Async-$i") { LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(500)) }
        }

        while (async.notCompletedCount() > 0) {
            val runningCount = async.runningCount()
            // 执行中数量必须小于等于线程数限制
            assertTrue(runningCount <= async.limit)
            LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(95))
        }
        assertEquals(max.toLong(), async.allCount())
    }

    @Test
    fun testMulti() {
        executor = ThreadUtils
        doTestMulti()
        executor = VirtualThread
        doTestMulti()
    }

    fun doTestMulti() {
        val max = 100000
        val async = Async(executor!!)
        for (i in 0 until max) {
            async.submit("Async-$i") { LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(1)) }
        }
        async.await()

        assertEquals(0, async.notCompletedCount())
        assertEquals(max.toLong(), async.allCount())
    }
}
