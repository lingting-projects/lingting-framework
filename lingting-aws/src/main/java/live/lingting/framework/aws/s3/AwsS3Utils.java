package live.lingting.framework.aws.s3;

import live.lingting.framework.time.DatePattern;
import lombok.experimental.UtilityClass;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author lingting 2024-09-19 15:20
 */
@UtilityClass
public class AwsS3Utils {

	/**
	 * 10M
	 */
	public static final long MULTIPART_DEFAULT_PART_SIZE = 10485760;

	/**
	 * 5G
	 */
	public static final long MULTIPART_MAX_PART_SIZE = 5368709120L;

	/**
	 * 100K
	 */
	public static final long MULTIPART_MIN_PART_SIZE = 102400;

	public static final long MULTIPART_MAX_PART_COUNT = 1000;

	public static final String PAYLOAD_UNSIGNED = "UNSIGNED-PAYLOAD";

	public static final String HEADER_PREFIX = "x-amz";

	public static final String HEADER_DATE = HEADER_PREFIX + "-date";

	public static final String HEADER_CONTENT_SHA256 = HEADER_PREFIX + "-content-sha256";

	public static final String HEADER_TOKEN = HEADER_PREFIX + "-security-token";

	public static final String HEADER_ACL = HEADER_PREFIX + "-acl";

	public static String format(LocalDateTime dateTime, DateTimeFormatter formatter) {
		ZonedDateTime atZone = dateTime.atZone(DatePattern.SYSTEM_ZONE_ID);
		ZonedDateTime atGmt = atZone.withZoneSameInstant(DatePattern.GMT_ZONE_ID);
		return formatter.format(atGmt);
	}
	public static LocalDateTime parse(String string, DateTimeFormatter formatter) {
		LocalDateTime source = LocalDateTime.parse(string, formatter);
		ZonedDateTime atGmt = source.atZone(DatePattern.GMT_ZONE_ID);
		ZonedDateTime atZone = atGmt.withZoneSameInstant(DatePattern.SYSTEM_ZONE_ID);
		return atZone.toLocalDateTime();
	}

	public static String encode(String s) {
		return URLEncoder.encode(s, StandardCharsets.UTF_8);
	}

}
