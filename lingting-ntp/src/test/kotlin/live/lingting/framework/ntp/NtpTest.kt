package live.lingting.framework.ntp

import live.lingting.framework.time.DatePattern
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * @author lingting 2024-01-29 17:38
 */
class NtpTest {
    @Test

    fun test() {
        val ntp = NtpFactory.create()
        assertNotNull(ntp)
        val now = ntp!!.current()
        assertNotNull(now)
        assertEquals(DatePattern.SYSTEM_ZONE_ID, ntp.zoneId)
        val millis = ntp.millis()
        assertTrue(millis > 0)
        assertTrue { ntp.host.isNotBlank() }
        assertNotNull(ntp.diff)
    }
}
