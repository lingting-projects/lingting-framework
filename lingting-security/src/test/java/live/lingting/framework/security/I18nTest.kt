package live.lingting.framework.security

import java.util.Locale
import live.lingting.framework.i18n.I18n
import live.lingting.framework.security.domain.SecurityResultCode
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * @author lingting 2024/12/3 20:30
 */
class I18nTest {

    @Test
    fun test() {
        I18n.set(Locale.CHINESE)
        assertEquals("非法登录!", SecurityResultCode.A_LOGIN_ILLEGAL.i18nMessage())
    }
}
