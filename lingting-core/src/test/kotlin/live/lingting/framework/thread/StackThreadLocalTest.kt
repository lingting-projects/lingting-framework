package live.lingting.framework.thread

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory


/**
 * @author lingting 2024-03-29 13:38
 */
internal class StackThreadLocalTest {
    @Test
    fun test() {
        val max = 1000
        val async = Async()
        for (i in 0 until max) {
            async.submit("stack-$i") {
                assertStack()
                assertStack()
                assertStack()
            }
        }

        async.await()
        Assertions.assertEquals(max.toLong(), async.allCount())
    }

    fun assertStack() {
        val id = Thread.currentThread().threadId()
        Assertions.assertNull(local.get())
        local.put(id)
        Assertions.assertEquals(id, local.get())
        Assertions.assertEquals(id, local.pop())
        Assertions.assertNull(local.get())
        Assertions.assertNull(local.pop())
        local.put(null)
        Assertions.assertNull(local.get())
        Assertions.assertNull(local.pop())
    }

    companion object {
        val local: StackThreadLocal<Long?> = StackThreadLocal()
        private val log: Logger = LoggerFactory.getLogger(StackThreadLocalTest::class.java)
    }
}
