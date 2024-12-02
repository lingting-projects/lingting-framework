package live.lingting.framework.sensitive

import live.lingting.framework.sensitive.SensitiveUtils.serialize
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * @author lingting 2024-01-26 18:01
 */
internal class SensitiveUtilsTest {
    @Test
    fun test() {
        val raw = "这是一个要脱敏的文本"
        val r = serialize(raw, 1, 4)
        assertEquals(raw, r)
        SensitiveHolder.desensitization()
        val r1 = serialize(raw, 1, 1)
        assertEquals("这******本", r1)
        val r2 = serialize(raw, 2, 2)
        assertEquals("这是******文本", r2)
        val r12 = serialize(raw, 1, 2)
        assertEquals("这******文本", r12)
        val r21 = serialize(raw, 2, 1)
        assertEquals("这是******本", r21)
    }
}
