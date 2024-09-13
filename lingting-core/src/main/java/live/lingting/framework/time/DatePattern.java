package live.lingting.framework.time;

import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * @author lingting
 */
@UtilityClass
public class DatePattern {

	public static final ZoneOffset DEFAULT_ZONE_OFFSET = ZoneOffset.of("+8");

	public static final ZoneId DEFAULT_ZONE_ID = DEFAULT_ZONE_OFFSET.normalized();

	public static final ZoneOffset GZM_ZONE_OFFSET = ZoneOffset.of("+0");

	public static final ZoneId GZM_ZONE_ID = GZM_ZONE_OFFSET.normalized();

	public static final ZoneId SYSTEM_ZONE_ID = ZoneId.systemDefault();

	public static final ZoneOffset SYSTEM_ZONE_OFFSET = SYSTEM_ZONE_ID.getRules().getOffset(Instant.now());

	public static final String NORM_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

	public static final String NORM_DATE_PATTERN = "yyyy-MM-dd";

	public static final String NORM_TIME_PATTERN = "HH:mm:ss";

	public static final DateTimeFormatter FORMATTER_YMD_HMS = DateTimeFormatter.ofPattern(NORM_DATETIME_PATTERN);

	public static final DateTimeFormatter FORMATTER_YMD = DateTimeFormatter.ofPattern(NORM_DATE_PATTERN);

	public static final DateTimeFormatter FORMATTER_HMS = DateTimeFormatter.ofPattern(NORM_TIME_PATTERN);

}
