package live.lingting.framework.kt

import java.time.Duration
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalUnit

fun Number.duration(unit: TemporalUnit): Duration = Duration.of(this.toLong(), unit)

inline val Number.milliseconds: Duration get() = this.duration(ChronoUnit.MILLIS)

inline val Number.seconds: Duration get() = this.duration(ChronoUnit.SECONDS)

inline val Number.minutes: Duration get() = this.duration(ChronoUnit.MINUTES)

inline val Number.hours: Duration get() = this.duration(ChronoUnit.HOURS)

inline val Number.days: Duration get() = this.duration(ChronoUnit.DAYS)

inline val Number.weeks: Duration get() = this.duration(ChronoUnit.WEEKS)

inline val Number.months: Duration get() = this.duration(ChronoUnit.MONTHS)

inline val Number.years: Duration get() = this.duration(ChronoUnit.YEARS)
