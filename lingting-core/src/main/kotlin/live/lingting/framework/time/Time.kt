package live.lingting.framework.time

import java.time.LocalDateTime
import java.time.temporal.TemporalAmount
import java.time.temporal.TemporalUnit

/**
 * @author lingting 2024-05-29 20:30
 */
class Time private constructor() {
    init {
        throw UnsupportedOperationException("This is a utility class and cannot be instantiated")
    }

    companion object {
        /**
         * 当前系统时间比实际真实时间慢多少毫秒
         */
        private var diff: Long = 0

        fun setDiff(diff: Long) {
            Companion.diff = diff
        }

        fun currentTimestamp(): Long {
            return System.currentTimeMillis() + diff
        }

        fun current(): LocalDateTime {
            return parse(currentTimestamp())
        }

        fun plus(amount: TemporalAmount): LocalDateTime {
            return current().plus(amount)
        }

        fun plus(amountToAdd: Long, unit: TemporalUnit): LocalDateTime {
            return current().plus(amountToAdd, unit)
        }

        fun plusYears(years: Long): LocalDateTime {
            return current().plusYears(years)
        }

        fun plusMonths(months: Long): LocalDateTime {
            return current().plusMonths(months)
        }

        fun plusWeeks(weeks: Long): LocalDateTime {
            return current().plusWeeks(weeks)
        }

        fun plusDays(days: Long): LocalDateTime {
            return current().plusDays(days)
        }

        fun plusHours(hours: Long): LocalDateTime {
            return current().plusHours(hours)
        }

        fun plusMinutes(minutes: Long): LocalDateTime {
            return current().plusMinutes(minutes)
        }

        fun plusSeconds(seconds: Long): LocalDateTime {
            return current().plusSeconds(seconds)
        }

        fun plusNanos(nanos: Long): LocalDateTime {
            return current().plusNanos(nanos)
        }

        fun minus(amount: TemporalAmount): LocalDateTime {
            return current().minus(amount)
        }

        fun minus(amountToAdd: Long, unit: TemporalUnit): LocalDateTime {
            return current().minus(amountToAdd, unit)
        }

        fun minusYears(years: Long): LocalDateTime {
            return current().minusYears(years)
        }

        fun minusMonths(months: Long): LocalDateTime {
            return current().minusMonths(months)
        }

        fun minusWeeks(weeks: Long): LocalDateTime {
            return current().minusWeeks(weeks)
        }

        fun minusDays(days: Long): LocalDateTime {
            return current().minusDays(days)
        }

        fun minusHours(hours: Long): LocalDateTime {
            return current().minusHours(hours)
        }

        fun minusMinutes(minutes: Long): LocalDateTime {
            return current().minusMinutes(minutes)
        }

        fun minusSeconds(seconds: Long): LocalDateTime {
            return current().minusSeconds(seconds)
        }

        fun minusNanos(nanos: Long): LocalDateTime {
            return current().minusNanos(nanos)
        }

        fun format(): String {
            return format(current())
        }
    }
}
