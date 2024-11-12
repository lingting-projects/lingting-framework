package live.lingting.framework.ntp

import live.lingting.framework.util.ThreadUtils
import live.lingting.framework.value.WaitValue
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.concurrent.TimeUnit
import java.util.function.UnaryOperator

/**
 * 中国 ntp 类
 *
 * @author lingting 2023/2/1 14:10
 */
class NtpCn private constructor() {
    init {
        throw UnsupportedOperationException("This is a utility class and cannot be instantiated")
    }


    companion object {
        val DEFAULT_ZONE_OFFSET: ZoneOffset = ZoneOffset.of("+8")

        val DEFAULT_ZONE_ID: ZoneId = DEFAULT_ZONE_OFFSET.normalized()

        val instance: WaitValue<Ntp> = WaitValue.of()
        private val log: Logger = LoggerFactory.getLogger(NtpCn::class.java)

        fun initNtpCN() {
            val factory: NtpFactory = NtpFactory.Companion.getDefault()
            val hosts: Set<String> = NtpFactory.Companion.getDefaultHosts()

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


        fun instance(): Ntp? {
            if (!instance.isNull) {
                return instance.getValue()
            }

            return instance.compute(object : UnaryOperator<Ntp?> {
                override fun apply(v: Ntp): Ntp {
                    if (v != null) {
                        return v
                    }

                    ThreadUtils.execute("NTP-INIT") { initNtpCN() }
                    return instance.notNull()!!
                }
            })
        }

        fun diff(): Long {
            return instance()!!.diff()
        }

        @JvmStatic
        fun currentMillis(): Long {
            return instance()!!.currentMillis()
        }

        fun instant(): Instant? {
            return instance()!!.instant()
        }

        fun now(): LocalDateTime {
            return instance()!!.now()
        }

        fun plusSeconds(seconds: Long): Long {
            return plusMillis(seconds * 1000)
        }

        fun plusMillis(millis: Long): Long {
            return instance()!!.plusMillis(millis)
        }

        fun plus(time: Long, unit: TimeUnit): Long {
            return plusMillis(unit.toMillis(time))
        }
    }
}
