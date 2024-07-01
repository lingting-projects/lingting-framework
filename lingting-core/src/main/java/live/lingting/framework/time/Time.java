package live.lingting.framework.time;

import live.lingting.framework.util.LocalDateTimeUtils;
import lombok.Getter;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;

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

	public static LocalDateTime plus(long amountToAdd, TemporalUnit unit) {return current().plus(amountToAdd, unit);}

	public static LocalDateTime plusYears(long years) {return current().plusYears(years);}

	public static LocalDateTime plusMonths(long months) {return current().plusMonths(months);}

	public static LocalDateTime plusWeeks(long weeks) {return current().plusWeeks(weeks);}

	public static LocalDateTime plusDays(long days) {return current().plusDays(days);}

	public static LocalDateTime plusHours(long hours) {return current().plusHours(hours);}

	public static LocalDateTime plusMinutes(long minutes) {return current().plusMinutes(minutes);}

	public static LocalDateTime plusSeconds(long seconds) {return current().plusSeconds(seconds);}

	public static LocalDateTime plusNanos(long nanos) {return current().plusNanos(nanos);}

	public static LocalDateTime minus(TemporalAmount amount) {
		return current().minus(amount);
	}

	public static LocalDateTime minus(long amountToAdd, TemporalUnit unit) {return current().minus(amountToAdd, unit);}

	public static LocalDateTime minusYears(long years) {return current().minusYears(years);}

	public static LocalDateTime minusMonths(long months) {return current().minusMonths(months);}

	public static LocalDateTime minusWeeks(long weeks) {return current().minusWeeks(weeks);}

	public static LocalDateTime minusDays(long days) {return current().minusDays(days);}

	public static LocalDateTime minusHours(long hours) {return current().minusHours(hours);}

	public static LocalDateTime minusMinutes(long minutes) {return current().minusMinutes(minutes);}

	public static LocalDateTime minusSeconds(long seconds) {return current().minusSeconds(seconds);}

	public static LocalDateTime minusNanos(long nanos) {return current().minusNanos(nanos);}

	public static String format() {
		return LocalDateTimeUtils.format(current());
	}

}
