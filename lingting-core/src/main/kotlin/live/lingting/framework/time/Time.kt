package live.lingting.framework.time

import java.time.LocalDateTime
import java.time.temporal.TemporalAmount
import java.time.temporal.TemporalUnit
import live.lingting.framework.util.LocalDateTimeUtils

/**
 * @author lingting 2024-05-29 20:30
 */
object Time {
    /**
     * 当前系统时间比实际真实时间慢多少毫秒
     */
    private var diff: Long = 0

    @JvmStatic
    fun setDiff(diff: Long) {
        this.diff = diff
    }

    @JvmStatic
    fun currentTimestamp(): Long {
        return System.currentTimeMillis() + diff
    }

    @JvmStatic
    fun current(): LocalDateTime {
        val timestamp = currentTimestamp()
        return LocalDateTimeUtils.parse(timestamp)
    }

    @JvmStatic
    fun plus(amount: TemporalAmount): LocalDateTime {
        return current().plus(amount)
    }

    @JvmStatic
    fun plus(amountToAdd: Long, unit: TemporalUnit): LocalDateTime {
        return current().plus(amountToAdd, unit)
    }

    @JvmStatic
    fun plusYears(years: Long): LocalDateTime {
        return current().plusYears(years)
    }

    @JvmStatic
    fun plusMonths(months: Long): LocalDateTime {
        return current().plusMonths(months)
    }

    @JvmStatic
    fun plusWeeks(weeks: Long): LocalDateTime {
        return current().plusWeeks(weeks)
    }

    @JvmStatic
    fun plusDays(days: Long): LocalDateTime {
        return current().plusDays(days)
    }

    @JvmStatic
    fun plusHours(hours: Long): LocalDateTime {
        return current().plusHours(hours)
    }

    @JvmStatic
    fun plusMinutes(minutes: Long): LocalDateTime {
        return current().plusMinutes(minutes)
    }

    @JvmStatic
    fun plusSeconds(seconds: Long): LocalDateTime {
        return current().plusSeconds(seconds)
    }

    @JvmStatic
    fun plusNanos(nanos: Long): LocalDateTime {
        return current().plusNanos(nanos)
    }

    @JvmStatic
    fun minus(amount: TemporalAmount): LocalDateTime {
        return current().minus(amount)
    }

    @JvmStatic
    fun minus(amountToAdd: Long, unit: TemporalUnit): LocalDateTime {
        return current().minus(amountToAdd, unit)
    }

    @JvmStatic
    fun minusYears(years: Long): LocalDateTime {
        return current().minusYears(years)
    }

    @JvmStatic
    fun minusMonths(months: Long): LocalDateTime {
        return current().minusMonths(months)
    }

    @JvmStatic
    fun minusWeeks(weeks: Long): LocalDateTime {
        return current().minusWeeks(weeks)
    }

    @JvmStatic
    fun minusDays(days: Long): LocalDateTime {
        return current().minusDays(days)
    }

    @JvmStatic
    fun minusHours(hours: Long): LocalDateTime {
        return current().minusHours(hours)
    }

    @JvmStatic
    fun minusMinutes(minutes: Long): LocalDateTime {
        return current().minusMinutes(minutes)
    }

    @JvmStatic
    fun minusSeconds(seconds: Long): LocalDateTime {
        return current().minusSeconds(seconds)
    }

    @JvmStatic
    fun minusNanos(nanos: Long): LocalDateTime {
        return current().minusNanos(nanos)
    }

    @JvmStatic
    fun format(): String {
        val current = current()
        return LocalDateTimeUtils.format(current)
    }
}

