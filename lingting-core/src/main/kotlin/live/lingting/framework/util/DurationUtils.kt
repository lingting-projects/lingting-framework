package live.lingting.framework.util

import java.time.Duration
import java.time.temporal.ChronoUnit.DAYS
import java.time.temporal.ChronoUnit.HOURS
import java.time.temporal.ChronoUnit.MILLIS
import java.time.temporal.ChronoUnit.MINUTES
import java.time.temporal.ChronoUnit.MONTHS
import java.time.temporal.ChronoUnit.SECONDS
import java.time.temporal.ChronoUnit.WEEKS
import java.time.temporal.ChronoUnit.YEARS
import java.time.temporal.TemporalUnit

object DurationUtils {

    /**
     * 浮点数使用 double 运行, 会丢失一定精度
     */
    @JvmStatic
    fun Number.duration(source: TemporalUnit): Duration {
        val (i, unit) = when (source) {
            WEEKS -> 7 to DAYS
            MONTHS -> 30 to DAYS
            YEARS -> 365 to DAYS
            else -> 1 to source
        }
        if (this is Float || this is Double) {
            val double = toDouble()
            val nanos = unit.duration.toNanos()
            val value = double * nanos * i
            return Duration.ofNanos(value.toLong())
        }

        return Duration.of(toLong() * i, unit)
    }

    @JvmStatic
    inline val Number.millis: Duration get() = this.duration(MILLIS)

    @JvmStatic
    inline val Number.seconds: Duration get() = this.duration(SECONDS)

    @JvmStatic
    inline val Number.minutes: Duration get() = this.duration(MINUTES)

    @JvmStatic
    inline val Number.hours: Duration get() = this.duration(HOURS)

    @JvmStatic
    inline val Number.days: Duration get() = this.duration(DAYS)

    /**
     * 等价于7天
     */
    @JvmStatic
    inline val Number.weeks: Duration get() = this.duration(WEEKS)

    /**
     * 等价于30天
     */
    @JvmStatic
    inline val Number.months: Duration get() = this.duration(MONTHS)

    /**
     * 等价于365天
     */
    @JvmStatic
    inline val Number.years: Duration get() = this.duration(YEARS)

}

