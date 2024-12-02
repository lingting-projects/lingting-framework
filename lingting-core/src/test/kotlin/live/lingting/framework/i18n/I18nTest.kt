package live.lingting.framework.i18n

import java.util.Locale
import live.lingting.framework.api.ApiResultCode
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * @author lingting 2024/12/2 16:03
 */
class I18nTest {

    @Test
    fun test() {
        assertEquals(TestSpiI18nProvider.value, I18n.find(TestSpiI18nProvider.key, TestSpiI18nProvider.locale))
        assertEquals("成功", ApiResultCode.SUCCESS.i18nMessage(Locale.CHINESE))
        assertEquals("成功", ApiResultCode.SUCCESS.i18nMessage(Locale.SIMPLIFIED_CHINESE))
    }

}
