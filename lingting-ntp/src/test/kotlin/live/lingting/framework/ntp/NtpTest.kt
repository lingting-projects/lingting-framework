package live.lingting.framework.ntp

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * @author lingting 2024-01-29 17:38
 */
internal class NtpTest {
    @Test
    @Throws(InterruptedException::class)
    fun test() {
        val instance: NtpFactory = NtpFactory.DEFAULT
        val ntp = instance.create()
        assertNotNull(ntp)
        val now = ntp!!.now()
        assertNotNull(now)
        assertEquals(Ntp.DEFAULT_ZONE_ID, ntp.zoneId)
        val millis = ntp.currentMillis()
        assertTrue(millis > 0)
    }
}
