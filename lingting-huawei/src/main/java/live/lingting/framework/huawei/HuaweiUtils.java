package live.lingting.framework.huawei;

import live.lingting.framework.http.HttpClient;
import lombok.experimental.UtilityClass;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * @author lingting 2024-09-13 11:54
 */
@UtilityClass
public class HuaweiUtils {

	public static final HttpClient CLIENT = HttpClient.okhttp()
		.disableSsl()
		.timeout(Duration.ofSeconds(5), Duration.ofSeconds(5))
		.build();

	public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

	public static final Duration TOKEN_EARLY_EXPIRE = Duration.ofMinutes(15);

	public static final Duration CREDENTIAL_EXPIRE = Duration.ofDays(1);

	public static LocalDateTime parse(String str, ZoneOffset zone) {
		LocalDateTime parse = LocalDateTime.parse(str, FORMATTER);
		return parse.plusSeconds(zone.getTotalSeconds());
	}

}
