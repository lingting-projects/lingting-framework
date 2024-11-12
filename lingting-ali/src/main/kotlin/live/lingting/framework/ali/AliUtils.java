package live.lingting.framework.ali;

import live.lingting.framework.time.DatePattern;
import org.slf4j.Logger;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static live.lingting.framework.time.DatePattern.FORMATTER_ISO_8601;

/**
 * @author lingting 2024-09-14 13:42
 */
public final class AliUtils {

	public static final Duration CREDENTIAL_EXPIRE = Duration.ofHours(1);

	public static final String HEADER_ERR = "x-oss-err";

	public static final String HEADER_EC = "x-oss-ec";
	private static final Logger log = org.slf4j.LoggerFactory.getLogger(AliUtils.class);

	private AliUtils() {throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");}

	public static LocalDateTime parse(String str) {
		LocalDateTime parse = LocalDateTime.parse(str, FORMATTER_ISO_8601);
		ZonedDateTime atGmt = parse.atZone(DatePattern.GMT_ZONE_ID);
		ZonedDateTime atSystem = atGmt.withZoneSameInstant(DatePattern.SYSTEM_ZONE_ID);
		return atSystem.toLocalDateTime();
	}

	public static String format(LocalDateTime dateTime, DateTimeFormatter formatter) {
		ZonedDateTime atZone = dateTime.atZone(DatePattern.SYSTEM_ZONE_ID);
		ZonedDateTime atGmt = atZone.withZoneSameInstant(DatePattern.GMT_ZONE_ID);
		return formatter.format(atGmt);
	}

}
