package live.lingting.framework.huawei;

import live.lingting.framework.aws.s3.AwsS3Utils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import static java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME;

/**
 * @author lingting 2024-09-13 11:54
 */
public final class HuaweiUtils {

	public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

	public static final Duration TOKEN_EARLY_EXPIRE = Duration.ofMinutes(15);

	public static final Duration CREDENTIAL_EXPIRE = Duration.ofDays(1);

	public static final Charset CHARSET = StandardCharsets.UTF_8;

	public static final String HEADER_DATE = "Date";

	private HuaweiUtils() {throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");}

	public static LocalDateTime parse(String str, ZoneOffset zone) {
		LocalDateTime parse = LocalDateTime.parse(str, FORMATTER);
		return parse.plusSeconds(zone.getTotalSeconds());
	}

	public static String format(LocalDateTime dateTime) {
		return AwsS3Utils.format(dateTime, RFC_1123_DATE_TIME);
	}

	public static LocalDateTime parse(String string) {
		return AwsS3Utils.parse(string, RFC_1123_DATE_TIME);
	}

}
