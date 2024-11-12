package live.lingting.framework.time;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * @author lingting
 */
public final class DatePattern {

	public static final ZoneOffset DEFAULT_ZONE_OFFSET = ZoneOffset.of("+8");

	public static final ZoneId DEFAULT_ZONE_ID = DEFAULT_ZONE_OFFSET.normalized();

	public static final ZoneOffset GMT_ZONE_OFFSET = ZoneOffset.of("+0");

	public static final ZoneId GMT_ZONE_ID = GMT_ZONE_OFFSET.normalized();

	public static final ZoneId SYSTEM_ZONE_ID = ZoneId.systemDefault();

	public static final ZoneOffset SYSTEM_ZONE_OFFSET = SYSTEM_ZONE_ID.getRules().getOffset(Instant.now());

	public static final String NORM_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

	public static final String NORM_DATE_PATTERN = "yyyy-MM-dd";

	public static final String NORM_TIME_PATTERN = "HH:mm:ss";

	public static final String ISO_8601_DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";

	public static final DateTimeFormatter FORMATTER_YMD_HMS = DateTimeFormatter.ofPattern(NORM_DATETIME_PATTERN);

	public static final DateTimeFormatter FORMATTER_YMD = DateTimeFormatter.ofPattern(NORM_DATE_PATTERN);

	public static final DateTimeFormatter FORMATTER_HMS = DateTimeFormatter.ofPattern(NORM_TIME_PATTERN);

	public static final DateTimeFormatter FORMATTER_ISO_8601 = DateTimeFormatter.ofPattern(ISO_8601_DATETIME_PATTERN);

	private DatePattern() {throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");}
}
