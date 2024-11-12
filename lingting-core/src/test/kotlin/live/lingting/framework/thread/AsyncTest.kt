package live.lingting.framework.thread

import live.lingting.framework.time.StopWatch
import live.lingting.framework.util.ThreadUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.LockSupport

/**
 * @author lingting 2024-01-26 17:07
 */
internal class AsyncTest {
    var executor: Executor? = null

    @Test
    fun test() {
        executor = ThreadUtils.executor()
        doTest()
        executor = VirtualThread.executor()
        doTest()
    }

    fun doTest() {
        val max = 10

        val watch = StopWatch()
        watch.start()

        val async = Async(executor)
        for (i in 0 until max) {
            async.submit("Async-$i") { LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(500)) }
        }
        async.await()
        watch.stop()

        Assertions.assertTrue(watch.timeMillis() > 500)
        Assertions.assertEquals(0, async.notCompletedCount())
        Assertions.assertEquals(max.toLong(), async.allCount())
    }

    @Test
    fun testLimit() {
        executor = ThreadUtils.executor()
        doTestLimit()
        executor = VirtualThread.executor()
        doTestLimit()
    }

    fun doTestLimit() {
        val limit: Long = 5
        val max = 10
        val async = Async(executor, limit)
        for (i in 0 until max) {
            async.submit("Async-$i") { LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(500)) }
        }

        while (async.notCompletedCount() > 0) {
            val runningCount = async.runningCount()
            // 执行中数量必须小于等于线程数限制
            Assertions.assertTrue(runningCount <= async.limit)
            LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(95))
        }
        Assertions.assertEquals(max.toLong(), async.allCount())
    }

    @Test
    fun testMulti() {
        executor = ThreadUtils.executor()
        doTestMulti()
        executor = VirtualThread.executor()
        doTestMulti()
    }

    fun doTestMulti() {
        val max = 100000
        val async = Async(executor)
        for (i in 0 until max) {
            async.submit("Async-$i") { LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(1)) }
        }
        async.await()

        Assertions.assertEquals(0, async.notCompletedCount())
        Assertions.assertEquals(max.toLong(), async.allCount())
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(AsyncTest::class.java)
    }
}
