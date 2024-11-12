package live.lingting.framework.ntp;

import org.slf4j.Logger;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.concurrent.TimeUnit;

/**
 * ntp 校时服务
 *
 * @author lingting 2022/11/18 13:40
 */
public class Ntp {

	public static final ZoneOffset DEFAULT_ZONE_OFFSET = ZoneOffset.of("+0");

	public static final ZoneId DEFAULT_ZONE_ID = DEFAULT_ZONE_OFFSET.normalized();
	private static final Logger log = org.slf4j.LoggerFactory.getLogger(Ntp.class);

	private final String host;

	private final long diff;

	private ZoneId zoneId = DEFAULT_ZONE_ID;

	public Ntp(String host, long diff) {
		this.host = host;
		this.diff = diff;
	}

	public Ntp zoneId(ZoneId zoneId) {
		if (zoneId == null) {
			throw new NtpException("ZoneId must be not null!");
		}
		this.zoneId = zoneId;
		return this;
	}

	public long currentMillis() {
		return System.currentTimeMillis() + diff;
	}

	public long diff() {
		return diff;
	}

	public Instant instant() {
		long millis = currentMillis();
		return Instant.ofEpochMilli(millis);
	}

	public LocalDateTime now() {
		Instant instant = instant();
		return LocalDateTime.ofInstant(instant, zoneId);
	}

	public long plusSeconds(long seconds) {
		return plusMillis(seconds * 1000);
	}

	public long plusMillis(long millis) {
		return currentMillis() + millis;
	}

	public long plus(long time, TimeUnit unit) {
		return plusMillis(unit.toMillis(time));
	}

	public String getHost() {return this.host;}

	public long getDiff() {return this.diff;}

	public ZoneId getZoneId() {return this.zoneId;}
}
