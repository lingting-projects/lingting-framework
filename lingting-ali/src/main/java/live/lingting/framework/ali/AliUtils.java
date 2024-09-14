package live.lingting.framework.ali;

import live.lingting.framework.http.HttpClient;
import live.lingting.framework.time.DatePattern;
import lombok.experimental.UtilityClass;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import static live.lingting.framework.time.DatePattern.FORMATTER_ISO_8601;

/**
 * @author lingting 2024-09-14 13:42
 */
@UtilityClass
public class AliUtils {

	public static final HttpClient CLIENT = HttpClient.okhttp()
		.disableSsl()
		.timeout(Duration.ofSeconds(15), Duration.ofSeconds(30))
		.build();

	public static final Duration CREDENTIAL_EXPIRE = Duration.ofHours(1);

	public static LocalDateTime parse(String str) {
		LocalDateTime parse = LocalDateTime.parse(str, FORMATTER_ISO_8601);
		ZonedDateTime atGmt = parse.atZone(DatePattern.GMT_ZONE_ID);
		ZonedDateTime atSystem = atGmt.withZoneSameInstant(DatePattern.SYSTEM_ZONE_ID);
		return atSystem.toLocalDateTime();
	}

	public static String date() {
		return format(LocalDateTime.now());
	}

	public static String format(LocalDateTime dateTime) {
		ZonedDateTime atZone = dateTime.atZone(DatePattern.SYSTEM_ZONE_ID);
		ZonedDateTime atGmt = atZone.withZoneSameInstant(DatePattern.GMT_ZONE_ID);
		return FORMATTER_ISO_8601.format(atGmt);
	}

}
