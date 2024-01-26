package live.lingting.framework.domain;

import lombok.experimental.UtilityClass;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * @author lingting
 */
@UtilityClass
public class DatePattern {

	public static final String NORM_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

	public static final String NORM_DATE_PATTERN = "yyyy-MM-dd";

	public static final String NORM_TIME_PATTERN = "HH:mm:ss";

	public static final ZoneOffset DEFAULT_ZONE_OFFSET = ZoneOffset.of("+8");

	public static final ZoneId DEFAULT_ZONE_ID = DEFAULT_ZONE_OFFSET.normalized();

	public static final DateTimeFormatter FORMATTER_YMD_HMS = DateTimeFormatter.ofPattern(NORM_DATETIME_PATTERN);

	public static final DateTimeFormatter FORMATTER_YMD = DateTimeFormatter.ofPattern(NORM_DATE_PATTERN);

	public static final DateTimeFormatter FORMATTER_HMS = DateTimeFormatter.ofPattern(NORM_TIME_PATTERN);

}
