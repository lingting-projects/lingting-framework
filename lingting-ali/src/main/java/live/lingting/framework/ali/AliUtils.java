package live.lingting.framework.ali;

import live.lingting.framework.http.HttpClient;
import live.lingting.framework.time.DatePattern;
import lombok.experimental.UtilityClass;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

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

	public static final String ISO_8601_DATETIME_PATTERN_I = "yyyyMMdd'T'HHmmss'Z'";

	public static final DateTimeFormatter FORMATTER_ISO_8601_D = DateTimeFormatter.ofPattern(ISO_8601_DATETIME_PATTERN_I);

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
