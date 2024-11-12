package live.lingting.polaris.grpc.util

import com.tencent.polaris.api.utils.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.Socket

/**
 * @author lixiaoshuang
 */
class NetworkHelper private constructor() {
    init {
        throw UnsupportedOperationException("This is a utility class and cannot be instantiated")
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(NetworkHelper::class.java)

        private const val LOCALHOST_VALUE = "127.0.0.1"

        /**
         * Gets the local address to which the socket is bound.
         *
         * @param host polaris server host
         * @param port polaris server port
         * @return local ip
         */
        fun getLocalHost(host: String?, port: Int): String? {
            try {
                Socket(host, port).use { socket ->
                    val address = socket.localAddress
                    return address.hostAddress
                }
            } catch (ex: IOException) {
                LOGGER.error("getLocalHost through socket fail : {}", ex.message)
                return localHostExactAddress
            }
        }

        val localHostExactAddress: String?
            /**
             * Get real local ip.
             *
             *
             * You can use getNetworkInterfaces()+getInetAddresses() to get all the IP addresses
             * of the node, and then judge to find out the site-local address, this is a
             * recommended solution.
             *
             * @return real ip
             */
            get() {
                try {
                    val networkInterfaces = NetworkInterface.getNetworkInterfaces()
                    while (networkInterfaces.hasMoreElements()) {
                        val iface = networkInterfaces.nextElement()
                        val inetAddrs = iface.inetAddresses
                        while (inetAddrs.hasMoreElements()) {
                            val inetAddr = inetAddrs.nextElement()
                            if (!inetAddr.isLoopbackAddress && inetAddr.isSiteLocalAddress) {
                                return inetAddr.hostAddress
                            }
                        }
                    }
                    return localHost
                } catch (e: Exception) {
                    LOGGER.error("getLocalHostExactAddress error", e)
                }
                return null
            }

        val localHost: String
            /**
             * Get local ip.
             *
             *
             * There are environmental restrictions. Different environments may get different ips.
             */
            get() {
                var inetAddress: InetAddress? = null
                try {
                    inetAddress = InetAddress.getLocalHost()
                } catch (e: Throwable) {
                    LOGGER.error("get local host", e)
                }
                if (inetAddress == null) {
                    return LOCALHOST_VALUE
                }
                return inetAddress.hostAddress
            }

        fun getUrlParams(param: String): Map<String, String?> {
            val map: MutableMap<String, String?> = HashMap()
            if (StringUtils.isBlank(param)) {
                return map
            }
            val params = param.split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (s in params) {
                val p = s.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                if (p.size == 2) {
                    map[p[0]] = p[1]
                }
            }
            return map
        }
    }
}
