package live.lingting.framework.ntp

import java.time.Instant

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.concurrent.TimeUnit

/**
 * ntp 校时服务
 * @author lingting 2022/11/18 13:40
 */
class Ntp(val host: String, val diff: Long) {
    var zoneId: ZoneId = DEFAULT_ZONE_ID
        private set

    fun zoneId(zoneId: ZoneId): Ntp {
        this.zoneId = zoneId
        return this
    }

    fun millis(): Long {
        return System.currentTimeMillis() + diff
    }

    fun diff(): Long {
        return diff
    }

    fun instant(): Instant {
        val millis = millis()
        return Instant.ofEpochMilli(millis)
    }

    fun current(): LocalDateTime {
        val instant = instant()
        return LocalDateTime.ofInstant(instant, zoneId)
    }

    fun plusSeconds(seconds: Long): Long {
        return plusMillis(seconds * 1000)
    }

    fun plusMillis(millis: Long): Long {
        return millis() + millis
    }

    fun plus(time: Long, unit: TimeUnit): Long {
        return plusMillis(unit.toMillis(time))
    }

    companion object {
        @JvmField
        val DEFAULT_ZONE_OFFSET: ZoneOffset = ZoneOffset.of("+0")

        @JvmField
        val DEFAULT_ZONE_ID: ZoneId = DEFAULT_ZONE_OFFSET.normalized()
    }
}
