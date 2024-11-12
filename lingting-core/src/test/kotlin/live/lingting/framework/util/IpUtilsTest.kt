package live.lingting.framework.util;

import org.junit.jupiter.api.Test;

import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author lingting 2023-12-21 14:09
 */
class IpUtilsTest {

	final String ip1 = "192.168.000.1";

	final String ip2 = "256.0.0.1";

	final String ip3 = "2001:0db8:85a3:0000:0000:8a2e:0370:7334";

	final String ip4 = "53543aa";

	@Test
	void isIpv4() {
		assertTrue(IpUtils.isIpv4(ip1));
		assertFalse(IpUtils.isIpv4(ip2));
		assertFalse(IpUtils.isIpv4(ip3));
		assertFalse(IpUtils.isIpv4(ip4));
	}

	@Test
	void isIpv6() {
		assertFalse(IpUtils.isIpv6(ip1));
		assertFalse(IpUtils.isIpv6(ip2));
		assertTrue(IpUtils.isIpv6(ip3));
		assertFalse(IpUtils.isIpv6(ip4));
	}

	@Test
	void resolve() throws UnknownHostException {
		String resolve = IpUtils.resolve("ntp.ntsc.ac.cn");
		System.out.println(resolve);
		assertNotNull(resolve);
	}

}
