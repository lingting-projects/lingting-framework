package live.lingting.framework.api

import live.lingting.framework.exception.BizException
import live.lingting.framework.i18n.I18n
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.Locale

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
        assertThrows(BizException::class.java) { with.throwException() }
        val exception = with.toException()
        assertTrue(exception is BizException)
        val biz = exception as BizException
        assertEquals(BizException.format(with, with.message), biz.message)
        assertEquals(with.code, biz.code)
    }
}
