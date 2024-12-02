package live.lingting.framework.util

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * @author lingting 2023-12-21 14:09
 */
internal class IpUtilsTest {
    val ip1: String = "192.168.000.1"

    val ip2: String = "256.0.0.1"

    val ip3: String = "2001:0db8:85a3:0000:0000:8a2e:0370:7334"

    val ip4: String = "53543aa"

    @Test
    fun isIpv4() {
        assertTrue(IpUtils.isIpv4(ip1))
        assertFalse(IpUtils.isIpv4(ip2))
        assertFalse(IpUtils.isIpv4(ip3))
        assertFalse(IpUtils.isIpv4(ip4))
    }

    @Test
    fun isIpv6() {
        assertFalse(IpUtils.isIpv6(ip1))
        assertFalse(IpUtils.isIpv6(ip2))
        assertTrue(IpUtils.isIpv6(ip3))
        assertFalse(IpUtils.isIpv6(ip4))
    }

    @Test

    fun resolve() {
        val resolve = IpUtils.resolve("ntp.ntsc.ac.cn")
        Assertions.assertNotNull(resolve)
    }
}
