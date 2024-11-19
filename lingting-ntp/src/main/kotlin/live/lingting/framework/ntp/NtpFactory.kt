package live.lingting.framework.ntp

import java.net.InetAddress
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.time.Duration
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import live.lingting.framework.util.IpUtils
import live.lingting.framework.util.ThreadUtils
import live.lingting.framework.value.CycleValue
import live.lingting.framework.value.StepValue
import live.lingting.framework.value.cycle.StepCycleValue
import live.lingting.framework.value.step.LongStepValue
import org.apache.commons.net.ntp.NTPUDPClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author lingting 2023-12-27 15:47
 */
class NtpFactory protected constructor() {
    private val blockHosts: MutableSet<String> = HashSet()


    fun create(): Ntp? {
        return create(HOSTS)
    }


    fun create(vararg hosts: String): Ntp? {
        return create(hosts.toSet())
    }


    fun create(hosts: Collection<String>): Ntp? {
        val cycle: CycleValue<Long> = StepCycleValue(STEP_INIT)
        for (host in hosts) {
            if (blockHosts.contains(host)) {
                log.debug("[{}] is block host! skip", host)
                continue
            }
            try {
                return createByFuture(cycle, host)
            } catch (e: UnknownHostException) {
                log.warn("[{}] host cannot be resolved!", host)
                blockHosts.add(host)
            } catch (e: InterruptedException) {
                throw e
            } catch (e: TimeoutException) {
                log.warn("Ntp initialization timeout! host: {}", host)
            } catch (e: Exception) {
                log.error("Ntp initialization exception! host: {}", host)
            }
        }
        return null
    }


    fun createByFuture(cycle: CycleValue<Long>, host: String): Ntp {
        val ip = IpUtils.resolve(host)

        var future: CompletableFuture<Ntp> = ThreadUtils.async {
            val diff = diff(host)
            Ntp(host, diff)
        }
        try {
            val next = cycle.next()
            val ntp = future[next, TimeUnit.SECONDS]
            if (ntp != null) {
                return ntp
            }
        } finally {
            future.cancel(true)
        }

        future = ThreadUtils.async { Ntp(ip, diff(ip)) }
        try {
            val next = cycle.next()
            return future[next, TimeUnit.SECONDS]
        } finally {
            future.cancel(true)
        }
    }

    fun diff(host: String): Long {
        try {
            NTPUDPClient().use { client ->
                client.setDefaultTimeout(Duration.ofSeconds(5))
                client.open()
                client.setSoTimeout(Duration.ofSeconds(3))
                val time = client.getTime(InetAddress.getByName(host))
                val system = System.currentTimeMillis()
                val ntp = time.message.transmitTimeStamp.time
                return ntp - system
            }
        } catch (e: SocketTimeoutException) {
            throw NtpException("Ntp get diff timeout! host: $host", e)
        } catch (e: Exception) {
            throw NtpException("Ntp get diff error! host: $host", e)
        }
    }

    companion object {
        @JvmField
        val STEP_INIT: StepValue<Long> = LongStepValue(1, null, 10)

        @JvmField
        val DEFAULT: NtpFactory = NtpFactory()

        @JvmField
        val HOSTS = setOf(
            "time.windows.com", "time.nist.gov", "time.apple.com",
            "time.asia.apple.com", "cn.ntp.org.cn", "ntp.ntsc.ac.cn", "cn.pool.ntp.org", "ntp.aliyun.com",
            "ntp1.aliyun.com", "ntp2.aliyun.com", "ntp3.aliyun.com", "ntp4.aliyun.com", "ntp5.aliyun.com",
            "ntp6.aliyun.com", "ntp7.aliyun.com",
        )
        private val log: Logger = LoggerFactory.getLogger(NtpFactory::class.java)

    }
}
