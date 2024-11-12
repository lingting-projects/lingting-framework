package live.lingting.framework.lock

import live.lingting.framework.thread.Async
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author lingting 2024-01-26 11:25
 */
internal class JavaReentrantLockTest {
    @Test
    fun test() {
        val lock = JavaReentrantLock()

        val value = AtomicInteger()
        val count = 2
        val async = Async()

        for (i in 0 until count) {
            async.submit("async-$i") {
                for (j in 0 until count) {
                    lock.run {
                        value.set(value.get() + 1)
                    }
                }
            }
        }

        async.await()
        Assertions.assertEquals(count * count, value.get())
    }
}
