package live.lingting.framework.retry

import live.lingting.framework.function.ThrowingSupplier
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.Duration
import java.util.concurrent.atomic.AtomicInteger

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

        var retry: Retry<Int?> = Retry.simple(4, Duration.ZERO, supplier)!!
        var value = retry.value()
        Assertions.assertTrue(value.success)
        Assertions.assertEquals(expected, value.get())
        Assertions.assertEquals(4, value.logs.size)

        atomic.set(0)
        retry = Retry.simple(2, Duration.ZERO, supplier)!!
        value = retry.value()
        Assertions.assertFalse(value.success)
        Assertions.assertNull(value.value)
        Assertions.assertEquals(3, value.logs.size)
    }
}
