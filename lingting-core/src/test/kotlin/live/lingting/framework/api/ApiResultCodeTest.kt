package live.lingting.framework.api

import java.util.Locale
import live.lingting.framework.exception.BizException
import live.lingting.framework.i18n.I18n
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * @author lingting 2024-01-25 16:02
 */
internal class ApiResultCodeTest {
    @Test
    fun test() {
        I18n.set(Locale.ENGLISH)
        val resultCode = ApiResultCode.SERVER_ERROR
        val r: R<Any> = R.failed(resultCode)
        assertEquals(resultCode.code, r.code)
        assertEquals(resultCode.message, r.message)
        val message = "testMessage"
        val with = resultCode.with(message)
        assertEquals(message, with.message)
        Assertions.assertThrows(BizException::class.java) { with.throwException() }
        val exception = with.toException()
        assertEquals(with.message, exception.message)
        assertEquals(with.code, exception.code)
    }
}
