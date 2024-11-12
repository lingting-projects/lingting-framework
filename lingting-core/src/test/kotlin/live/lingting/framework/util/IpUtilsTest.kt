package live.lingting.framework.util

import live.lingting.framework.retry.Retry.value
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.net.UnknownHostException

/**
 * @author lingting 2023-12-21 14:09
 */
internal class IpUtilsTest {
    val ip1: String = "192.168.000.1"

    val ip2: String = "256.0.0.1"

    val ip3: String = "2001:0db8:85a3:0000:0000:8a2e:0370:7334"

    val ip4: String = "53543aa"

    @get:Test
    val isIpv4: Unit
        get() {
            assertTrue(IpUtils.isIpv4(ip1))
            assertFalse(IpUtils.isIpv4(ip2))
            assertFalse(IpUtils.isIpv4(ip3))
            assertFalse(IpUtils.isIpv4(ip4))
        }

    @get:Test
    val isIpv6: Unit
        get() {
            assertFalse(IpUtils.isIpv6(ip1))
            assertFalse(IpUtils.isIpv6(ip2))
            assertTrue(IpUtils.isIpv6(ip3))
            assertFalse(IpUtils.isIpv6(ip4))
        }

    @Test
    @Throws(UnknownHostException::class)
    fun resolve() {
        val resolve = IpUtils.resolve("ntp.ntsc.ac.cn")
        println(resolve)
        Assertions.assertNotNull(resolve)
    }
}
