package live.lingting.framework.ntp;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author lingting 2024-01-29 17:38
 */
class NtpTest {

	@Test
	void test() throws InterruptedException {
		NtpFactory instance = NtpFactory.INSTANCE;
		Ntp ntp = instance.create();
		assertNotNull(ntp);
		LocalDateTime now = ntp.now();
		assertNotNull(now);
		assertEquals(Ntp.DEFAULT_ZONE_ID, ntp.getZoneId());
		long millis = NtpCn.currentMillis();
		assertTrue(millis > 0);
	}

}
