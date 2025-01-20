package live.lingting.framework.time

import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZonedDateTime

/**
 * @author lingting 2024-05-29 20:30
 */
object DateTime {

    /**
     * 内置的时区设置, 用于规避系统的时区设置问题, 默认使用系统时区.
     */
    var zoneId = DatePattern.SYSTEM_ZONE_ID
        private set

    var clock: Clock = Clock.system(zoneId)
        private set

    var zoneOffset = DatePattern.SYSTEM_ZONE_OFFSET
        set(value) {
            field = value
            zoneId = value.normalized()
            clock = clock.withZone(value)
        }

    @JvmStatic
    fun millis(): Long {
        return clock.millis()
    }

    @JvmStatic
    fun instant(): Instant {
        return clock.instant()
    }

    @JvmStatic
    fun zone(): ZonedDateTime {
        return instant().atZone(zoneId)
    }

    @JvmStatic
    fun current(): LocalDateTime {
        return zone().toLocalDateTime()
    }

    @JvmStatic
    fun date(): LocalDate {
        return zone().toLocalDate()
    }

    @JvmStatic
    fun time(): LocalTime {
        return zone().toLocalTime()
    }

}

