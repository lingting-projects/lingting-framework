package live.lingting.framework.util

import live.lingting.framework.util.LocaleUtils.parseLocale
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrowsExactly
import org.junit.jupiter.api.Test

/**
 * @author lingting 2024/12/6 14:52
 */
class LocaleUtilsTest {

    @Test
    fun test() {
        assertEquals("zh", "zh-CN".parseLocale().language)
        assertEquals("zh-CN", "zh-CN".parseLocale().toLanguageTag())
        assertEquals("zh-CN", "zh_CN".parseLocale().toLanguageTag())
        assertEquals("zh-Hans-CN", "zh hans CN".parseLocale().toLanguageTag())
        assertThrowsExactly(IllegalArgumentException::class.java) { "235".parseLocale() }
    }
}
