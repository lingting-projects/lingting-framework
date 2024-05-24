package live.lingting.framework.util;

import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static live.lingting.framework.time.DatePattern.DEFAULT_ZONE_ID;
import static live.lingting.framework.time.DatePattern.DEFAULT_ZONE_OFFSET;
import static live.lingting.framework.time.DatePattern.FORMATTER_HMS;
import static live.lingting.framework.time.DatePattern.FORMATTER_YMD;
import static live.lingting.framework.time.DatePattern.FORMATTER_YMD_HMS;

/**
 * @author lingting 2022/11/28 10:12
 */
@UtilityClass
public class LocalDateTimeUtils {

	// region LocalDateTime

	/**
	 * 字符串转时间
	 * @param str yyyy-MM-dd HH:mm:ss 格式字符串
	 * @return java.time.LocalDateTime 时间
	 */
	public static LocalDateTime parse(String str) {
		return LocalDateTime.parse(str, FORMATTER_YMD_HMS);
	}

	/**
	 * 时间戳转时间, 使用 GMT+8 时区
	 * @param timestamp 时间戳 - 毫秒
	 * @return java.time.LocalDateTime
	 */
	public static LocalDateTime parse(Long timestamp) {
		return parse(timestamp, DEFAULT_ZONE_ID);
	}

	/**
	 * 时间戳转时间
	 * @param timestamp 时间戳 - 毫秒
	 * @param zoneId 时区
	 * @return java.time.LocalDateTime
	 */
	public static LocalDateTime parse(Long timestamp, ZoneId zoneId) {
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), zoneId);
	}

	public static Long toTimestamp(LocalDateTime dateTime) {
		return toTimestamp(dateTime, DEFAULT_ZONE_OFFSET);
	}

	public static Long toTimestamp(LocalDateTime dateTime, ZoneOffset offset) {
		return dateTime.toInstant(offset).toEpochMilli();
	}

	public static String format(LocalDateTime dateTime) {
		return format(dateTime, FORMATTER_YMD_HMS);
	}

	public static String format(LocalDateTime dateTime, String formatter) {
		return format(dateTime, DateTimeFormatter.ofPattern(formatter));
	}

	public static String format(LocalDateTime dateTime, DateTimeFormatter formatter) {
		return formatter.format(dateTime);
	}

	// endregion

	// region LocalDate
	public static LocalDate parseDate(String str) {
		return LocalDate.parse(str, FORMATTER_YMD);
	}

	public static String format(LocalDate date) {
		return format(date, FORMATTER_YMD);
	}

	public static String format(LocalDate date, String formatter) {
		return format(date, DateTimeFormatter.ofPattern(formatter));
	}

	public static String format(LocalDate date, DateTimeFormatter formatter) {
		return formatter.format(date);
	}

	// endregion

	// region LocalTime
	public static LocalTime parseTime(String str) {
		return LocalTime.parse(str, FORMATTER_HMS);
	}

	public static String format(LocalTime time) {
		return format(time, FORMATTER_HMS);
	}

	public static String format(LocalTime time, String formatter) {
		return format(time, DateTimeFormatter.ofPattern(formatter));
	}

	public static String format(LocalTime time, DateTimeFormatter formatter) {
		return formatter.format(time);
	}

	// endregion

	// region Date

	public static Date toDate(LocalDateTime dateTime) {
		Long timestamp = toTimestamp(dateTime);
		return new Date(timestamp);
	}

	public static LocalDateTime parse(Date date) {
		return parse(date.getTime());
	}

	// endregion

}
