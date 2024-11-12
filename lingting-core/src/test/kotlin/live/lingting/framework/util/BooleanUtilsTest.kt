package live.lingting.framework.util

import org.junit.jupiter.api.Test

/**
 * @author lingting 2024-01-26 17:22
 */
internal class BooleanUtilsTest {
    @Test
    fun test() {
        assertTrue(isTrue("y"))
        assertFalse(isTrue("n"))
        assertTrue(isFalse("n"))
        assertFalse(isFalse("y"))
    }
}
