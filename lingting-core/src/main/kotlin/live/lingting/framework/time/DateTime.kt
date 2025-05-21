package live.lingting.framework.time

import live.lingting.framework.lock.JavaReentrantLock
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

    private var zoneIdValue = DatePattern.SYSTEM_ZONE_ID

    private var zoneOffsetValue = DatePattern.SYSTEM_ZONE_OFFSET

    private var clockValue = Clock.system(zoneIdValue)

    private val lock = JavaReentrantLock()

    /**
     * 内置的时区设置, 用于规避系统的时区设置问题, 默认使用系统时区.
     */
    var zoneId
        get() = zoneIdValue
        set(value) {
            lock.runByInterruptibly {
                clockValue = Clock.system(value)

                zoneIdValue = value
                zoneOffsetValue = value.rules.getOffset(Instant.now())
            }
        }

    var zoneOffset
        get() = zoneOffsetValue
        set(value) {
            lock.runByInterruptibly {
                clockValue = clock.withZone(value)

                zoneIdValue = value.normalized()
                zoneOffsetValue = value
            }
        }

    var clock: Clock
        get() = clockValue
        set(value) {
            lock.runByInterruptibly {
                zoneId = value.zone
            }
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

