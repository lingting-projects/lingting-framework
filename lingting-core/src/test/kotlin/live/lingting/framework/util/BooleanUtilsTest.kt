package live.lingting.framework.util

import live.lingting.framework.util.BooleanUtils.isFalse
import live.lingting.framework.util.BooleanUtils.isTrue
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * @author lingting 2024-01-26 17:22
 */
internal class BooleanUtilsTest {
    @Test
    fun test() {
        assertTrue("y".isTrue())
        assertFalse("n".isTrue())
        assertTrue("n".isFalse())
        assertFalse("y".isFalse())
    }
}
