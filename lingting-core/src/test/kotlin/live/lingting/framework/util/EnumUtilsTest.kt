package live.lingting.framework.util

import com.baomidou.mybatisplus.annotation.IEnum
import com.fasterxml.jackson.annotation.JsonValue
import org.junit.jupiter.api.Test

/**
 * @author lingting 2024-01-29 13:51
 */
internal class EnumUtilsTest {
    @Test
    fun test() {
        assertEquals(1, EnumUtils.getValue(IE.IE1))
        assertEquals(2, EnumUtils.getValue(IE.IE2))

        assertEquals(1, EnumUtils.getValue(JE.JE1))
        assertEquals(2, EnumUtils.getValue(JE.JE2))
    }

    internal enum class IE(private val value: Int) : IEnum<Int> {
        IE1(1), IE2(2),
        ;

        override fun getValue(): Int {
            return this.value
        }
    }

    internal enum class JE(@field:JsonValue val value: Int) {
        JE1(1), JE2(2),
    }
}
