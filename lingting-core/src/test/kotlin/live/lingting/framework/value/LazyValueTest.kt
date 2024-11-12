package live.lingting.framework.value

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

/**
 * @author lingting 2024-09-28 15:33
 */
internal class LazyValueTest {
    @Test
    fun test() {
        val lazyValue = LazyValue { "test" }
        Assertions.assertNull(lazyValue.t)
        Assertions.assertTrue(lazyValue.isFirst())
        Assertions.assertEquals("test", lazyValue.get())
        Assertions.assertEquals("test", lazyValue.t)
        Assertions.assertFalse(lazyValue.isFirst())
    }
}
