package live.lingting.framework.kt

import java.time.Duration
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalUnit

fun Number.jd(unit: TemporalUnit): Duration = Duration.of(this.toLong(), unit)

inline val Number.seconds: Duration get() = this.jd(ChronoUnit.SECONDS)
inline val Number.minutes: Duration get() = this.jd(ChronoUnit.MINUTES)
inline val Number.hours: Duration get() = this.jd(ChronoUnit.HOURS)
inline val Number.days: Duration get() = this.jd(ChronoUnit.DAYS)
inline val Number.weeks: Duration get() = this.jd(ChronoUnit.WEEKS)
inline val Number.months: Duration get() = this.jd(ChronoUnit.MONTHS)
inline val Number.years: Duration get() = this.jd(ChronoUnit.YEARS)

