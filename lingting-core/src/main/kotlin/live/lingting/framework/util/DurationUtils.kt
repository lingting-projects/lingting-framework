package live.lingting.framework.util

import java.time.Duration
import java.time.temporal.ChronoUnit.*
import java.time.temporal.TemporalUnit

object DurationUtils {

    @JvmStatic
    fun Number.duration(unit: TemporalUnit): Duration = Duration.of(this.toLong(), unit)

    @JvmStatic
    fun Number.millis() = this.duration(MILLIS)

    @JvmStatic
    fun Number.seconds() = this.duration(SECONDS)

    @JvmStatic
    fun Number.minutes() = this.duration(MINUTES)

    @JvmStatic
    fun Number.hours() = this.duration(HOURS)

    @JvmStatic
    fun Number.days() = this.duration(DAYS)

    @JvmStatic
    fun Number.weeks() = this.duration(WEEKS)

    @JvmStatic
    fun Number.months() = this.duration(MONTHS)

    @JvmStatic
    fun Number.years() = this.duration(YEARS)

    inline val Number.millis: Duration get() = this.duration(MILLIS)

    inline val Number.seconds: Duration get() = this.duration(SECONDS)

    inline val Number.minutes: Duration get() = this.duration(MINUTES)

    inline val Number.hours: Duration get() = this.duration(HOURS)

    inline val Number.days: Duration get() = this.duration(DAYS)

    inline val Number.weeks: Duration get() = this.duration(WEEKS)

    inline val Number.months: Duration get() = this.duration(MONTHS)

    inline val Number.years: Duration get() = this.duration(YEARS)

}

