package live.lingting.framework.value

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * @author lingting 2024-09-28 15:33
 */
internal class LazyValueTest {
    @Test
    fun test() {
        val lazyValue = LazyValue { "test" }
        assertNull(lazyValue.t)
        assertTrue(lazyValue.isFirst())
        assertEquals("test", lazyValue.get())
        assertEquals("test", lazyValue.t)
        assertFalse(lazyValue.isFirst())
    }
}
