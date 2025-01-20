package live.lingting.framework.ntp

import java.net.InetAddress
import java.net.SocketTimeoutException
import java.time.Duration
import java.util.concurrent.CopyOnWriteArrayList
import live.lingting.framework.thread.Async
import live.lingting.framework.time.DateTime
import live.lingting.framework.util.DurationUtils.millis
import live.lingting.framework.util.IpUtils
import live.lingting.framework.util.Slf4jUtils.logger
import live.lingting.framework.util.ValueUtils
import org.apache.commons.net.ntp.NTPUDPClient

/**
 * @author lingting 2023-12-27 15:47
 */
object NtpFactory {

    @JvmField
    val HOSTS = setOf(
        "time.windows.com", "time.nist.gov", "time.apple.com",
        "time.asia.apple.com", "cn.ntp.org.cn", "ntp.ntsc.ac.cn", "cn.pool.ntp.org", "ntp.aliyun.com",
        "ntp1.aliyun.com", "ntp2.aliyun.com", "ntp3.aliyun.com", "ntp4.aliyun.com", "ntp5.aliyun.com",
        "ntp6.aliyun.com", "ntp7.aliyun.com",
    )

    private val log = logger()

    fun create(): Ntp? {
        return create(HOSTS)
    }

    fun create(vararg hosts: String): Ntp? {
        return create(hosts.toSet())
    }

    fun create(hosts: Collection<String>): Ntp? {
        return create(null, hosts)
    }

    fun create(duration: Duration?, hosts: Collection<String>): Ntp? {
        val async = Async()
        val list = CopyOnWriteArrayList<Ntp>()
        for (host in hosts) {
            async.submit {
                diff(host)?.let {
                    list.add(Ntp(host, it.millis))
                }
            }

            async.submit {
                val ip = IpUtils.resolve(host)
                if (ip.isNotBlank()) {
                    diff(ip)?.let {
                        list.add(Ntp(ip, it.millis))
                    }
                }
            }
        }

        ValueUtils.awaitTrue(duration) { list.isNotEmpty() || async.notCompletedCount() < 1 }
        async.interruptAll()
        return list.firstOrNull()
    }

    /**
     * 返回指定ntp服务的时间偏移量. 毫秒
     */
    fun diff(host: String): Long? {
        try {
            NTPUDPClient().use { client ->
                client.setDefaultTimeout(Duration.ofSeconds(5))
                client.open()
                client.setSoTimeout(Duration.ofSeconds(3))
                val time = client.getTime(InetAddress.getByName(host))
                val system = DateTime.millis()
                val ntp = time.message.transmitTimeStamp.time
                return ntp - system
            }
        } catch (_: InterruptedException) {
            Thread.currentThread().interrupt()
        } catch (e: SocketTimeoutException) {
            log.debug("Ntp get diff timeout! host: $host", e)
        } catch (e: Exception) {
            log.debug("Ntp get diff exception! host: $host", e)
        }
        return null
    }

}
