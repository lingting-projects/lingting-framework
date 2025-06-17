package live.lingting.framework.ntp

import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import live.lingting.framework.time.DatePattern
import live.lingting.framework.time.DateTime

/**
 * ntp 校时结果
 * @author lingting 2022/11/18 13:40
 */
class Ntp(val host: String, val diff: Duration) {

    val zoneId: ZoneId = DatePattern.SYSTEM_ZONE_ID

    val clock: Clock = Clock.offset(Clock.system(zoneId), diff)

    fun millis(): Long {
        return clock.millis()
    }

    fun instant(): Instant {
        return clock.instant()
    }

    fun zone(): ZonedDateTime {
        return DateTime.instant().atZone(zoneId)
    }

    fun current(): LocalDateTime {
        return zone().toLocalDateTime()
    }

    fun date(): LocalDate {
        return zone().toLocalDate()
    }

    fun time(): LocalTime {
        return zone().toLocalTime()
    }

}
