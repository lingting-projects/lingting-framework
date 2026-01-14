package live.lingting.framework.context

import live.lingting.framework.thread.virtual.VirtualThread
import live.lingting.framework.value.WaitValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * @author lingting 2026/1/14 19:36
 */
class ContextTest {

    val c = Context<Any>()

    @Test
    fun test() {
        val waited = WaitValue.of<Any>()
        VirtualThread.submit {
            val id = Thread.currentThread().threadId()
            c.set(id)
            waited.update(id)
            Thread.sleep(500)
        }
        waited.notNull()
        assertEquals(1, c.size())
        Thread.sleep(1000)
        assertEquals(0, c.size())
    }

}
