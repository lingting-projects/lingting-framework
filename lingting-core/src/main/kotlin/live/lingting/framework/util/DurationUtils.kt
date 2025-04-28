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

    @JvmStatic
    fun Number.duration(unit: TemporalUnit): Duration = Duration.of(this.toLong(), unit)

    inline val Number.millis: Duration get() = this.duration(MILLIS)

    inline val Number.seconds: Duration get() = this.duration(SECONDS)

    inline val Number.minutes: Duration get() = this.duration(MINUTES)

    inline val Number.hours: Duration get() = this.duration(HOURS)

    inline val Number.days: Duration get() = this.duration(DAYS)

    inline val Number.weeks: Duration get() = this.duration(WEEKS)

    inline val Number.months: Duration get() = this.duration(MONTHS)

    inline val Number.years: Duration get() = this.duration(YEARS)

}

