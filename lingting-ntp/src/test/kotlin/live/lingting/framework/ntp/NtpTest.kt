package live.lingting.framework.ntp

import live.lingting.framework.ntp.NtpCn.Companion.currentMillis
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

/**
 * @author lingting 2024-01-29 17:38
 */
internal class NtpTest {
    @Test
    @Throws(InterruptedException::class)
    fun test() {
        val instance: NtpFactory = NtpFactory.INSTANCE
        val ntp = instance.create()
        Assertions.assertNotNull(ntp)
        val now = ntp!!.now()
        Assertions.assertNotNull(now)
        Assertions.assertEquals(Ntp.DEFAULT_ZONE_ID, ntp.zoneId)
        val millis = currentMillis()
        Assertions.assertTrue(millis > 0)
    }
}
