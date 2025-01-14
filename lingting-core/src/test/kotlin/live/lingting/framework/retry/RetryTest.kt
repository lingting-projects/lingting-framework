package live.lingting.framework.retry

import java.time.Duration
import java.util.concurrent.atomic.AtomicInteger
import live.lingting.framework.function.ThrowingSupplier
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * @author lingting 2023-10-24 14:33
 */
internal class RetryTest {
    @Test
    fun test() {
        val expected = 3

        val atomic = AtomicInteger(0)
        val supplier: ThrowingSupplier<Int> = ThrowingSupplier<Int> {
            val i = atomic.get()
            if (i == expected) {
                return@ThrowingSupplier i
            }
            atomic.set(i + 1)
            throw IllegalStateException("异常")
        }

        var retry: Retry<Int> = Retry.simple(4, Duration.ZERO, supplier)
        var log = retry.last()
        assertTrue(log.ex == null)
        assertEquals(expected, log.value)
        assertEquals(4, retry.logs.size)

        atomic.set(0)
        retry = Retry.simple(2, Duration.ZERO, supplier)
        log = retry.last()
        assertFalse(log.ex == null)
        assertNull(log.value)
        assertEquals(3, retry.logs.size)
    }
}
