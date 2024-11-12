package live.lingting.framework.sensitive

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

/**
 * @author lingting 2024-01-26 18:01
 */
internal class SensitiveUtilsTest {
    @Test
    fun test() {
        val raw = "这是一个要脱敏的文本"
        val r1: String = serialize(raw, 1, 1)
        println(r1)
        Assertions.assertEquals("这******本", r1)
        val r2: String = serialize(raw, 2, 2)
        println(r2)
        Assertions.assertEquals("这是******文本", r2)
        val r12: String = serialize(raw, 1, 2)
        println(r12)
        Assertions.assertEquals("这******文本", r12)
        val r21: String = serialize(raw, 2, 1)
        println(r21)
        Assertions.assertEquals("这是******本", r21)
    }
}
