package live.lingting.framework.thread

import java.time.Duration
import java.util.concurrent.Executor
import java.util.concurrent.atomic.AtomicInteger
import live.lingting.framework.context.ContextHolder.start
import live.lingting.framework.util.ThreadUtils
import live.lingting.framework.util.ValueUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * @author lingting 2023-12-20 21:53
 */
internal class AbstractTimerTest {
    var executor: Executor? = null

    @Test

    fun test() {
        executor = ThreadUtils.executor()
        doTest()
        executor = VirtualThread.executor()
        doTest()
    }


    fun doTest() {
        start()
        val atomic = AtomicInteger(0)

        val timer: AbstractTimer = object : AbstractTimer() {
            override fun executor(): Executor {
                return executor!!
            }

            override val timeout: Duration
                get() =// 设置执行间隔
                    Duration.ofMinutes(30)


            override fun process() {
                atomic.set(atomic.get() + 1)
            }
        }

        timer.onApplicationStart()
        ValueUtils.await({ atomic.get() }, { v -> v > 0 })
        assertEquals(1, atomic.get())
        timer.wake()
        ValueUtils.await({ atomic.get() }, { v -> v > 1 })
        assertEquals(2, atomic.get())
        val thread = timer.threadValue.value
        assertNotNull(thread)
        assertFalse(thread!!.isInterrupted)
        thread.interrupt()
        assertTrue(thread.isInterrupted)
    }
}
