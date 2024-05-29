package live.lingting.framework.time;

import live.lingting.framework.util.LocalDateTimeUtils;
import lombok.Getter;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAmount;

/**
 * @author lingting 2024-05-29 20:30
 */
@Getter
@UtilityClass
public class Time {

	/**
	 * 当前系统时间比实际真实时间慢多少毫秒
	 */
	private static long diff = 0;

	public static void setDiff(long diff) {
		Time.diff = diff;
	}

	public static long currentTimestamp() {
		return System.currentTimeMillis() + diff;
	}

	public static LocalDateTime current() {
		return LocalDateTimeUtils.parse(currentTimestamp());
	}

	public static LocalDateTime plus(TemporalAmount amount) {
		return current().plus(amount);
	}

	public static String format() {
		return LocalDateTimeUtils.format(current());
	}

}
