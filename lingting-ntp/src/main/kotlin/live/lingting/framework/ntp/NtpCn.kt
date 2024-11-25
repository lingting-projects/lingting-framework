package live.lingting.framework.ntp

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.concurrent.TimeUnit
import live.lingting.framework.kt.logger
import live.lingting.framework.util.ThreadUtils
import live.lingting.framework.value.WaitValue
import org.slf4j.Logger

/**
 * 中国 ntp 类
 * @author lingting 2023/2/1 14:10
 */
object NtpCn {
    @JvmField
    val DEFAULT_ZONE_OFFSET: ZoneOffset = ZoneOffset.of("+8")

    @JvmField
    val DEFAULT_ZONE_ID: ZoneId = DEFAULT_ZONE_OFFSET.normalized()

    @JvmField
    val instance: WaitValue<Ntp> = WaitValue.of()

    val log: Logger = logger()

    @JvmStatic
    fun initNtpCN() {
        val factory: NtpFactory = NtpFactory.DEFAULT
        val hosts: Set<String> = NtpFactory.HOSTS

        var index = 0
        while (instance.isNull) {
            index++

            val ntp = factory.create(hosts)
            if (ntp != null) {
                instance.update(ntp.zoneId(DEFAULT_ZONE_ID))
                return
            }
            log.warn("Ntp create failed, retry count: {}", index)
        }
    }

    fun instance(): Ntp {
        return instance.value ?: instance.compute {
            if (it != null) {
                return@compute it
            }

            ThreadUtils.execute("NTP-INIT") { initNtpCN() }
            return@compute instance.notNull()
        }!!
    }

    fun diff(): Long {
        return instance().diff()
    }

    @JvmStatic
    fun currentMillis(): Long {
        return instance().currentMillis()
    }

    fun instant(): Instant? {
        return instance().instant()
    }

    fun now(): LocalDateTime {
        return instance().now()
    }

    fun plusSeconds(seconds: Long): Long {
        return plusMillis(seconds * 1000)
    }

    fun plusMillis(millis: Long): Long {
        return instance().plusMillis(millis)
    }

    fun plus(time: Long, unit: TimeUnit): Long {
        return plusMillis(unit.toMillis(time))
    }
}

