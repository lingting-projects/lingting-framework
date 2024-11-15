package live.lingting.framework.kt

import java.time.Duration
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalUnit

fun Number.jd(unit: TemporalUnit): Duration = Duration.of(this.toLong(), unit)

inline val Number.s: Duration get() = this.jd(ChronoUnit.SECONDS)
inline val Number.m: Duration get() = this.jd(ChronoUnit.MINUTES)
inline val Number.h: Duration get() = this.jd(ChronoUnit.HOURS)
inline val Number.d: Duration get() = this.jd(ChronoUnit.DAYS)
inline val Number.w: Duration get() = this.jd(ChronoUnit.WEEKS)
inline val Number.M: Duration get() = this.jd(ChronoUnit.MONTHS)
inline val Number.Y: Duration get() = this.jd(ChronoUnit.YEARS)
