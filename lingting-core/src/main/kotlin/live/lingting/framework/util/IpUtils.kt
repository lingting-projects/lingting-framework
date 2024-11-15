package live.lingting.framework.util

import jakarta.servlet.http.HttpServletRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.InetAddress

import java.util.function.Predicate

/**
 * @author psh 2022-04-21 16:55
 */
class IpUtils private constructor() {
    init {
        throw UnsupportedOperationException("This is a utility class and cannot be instantiated")
    }

    companion object {
        const val LOCALHOST: String = "127.0.0.1"

        const val UNKNOWN: String = "UNKNOWN"

        const val MULTI_IP_SPLIT: String = ","

        const val IPV4_SPLIT: String = "."

        const val IPV6_SPLIT: String = ":"

        const val IPV4_LENGTH_MAX: Int = 16

        private val HEADERS: MutableList<String> = ArrayList(16)
        private val log: Logger = LoggerFactory.getLogger(IpUtils::class.java)

        init {
            HEADERS.add("X-Forwarded-For")
            HEADERS.add("Node-Forwarded-IP")
            HEADERS.add("X-Real-Ip")
            HEADERS.add("Proxy-Client-IP")
            HEADERS.add("WL-Proxy-Client-IP")
            HEADERS.add("HTTP_CLIENT_IP")
            HEADERS.add("HTTP_X_FORWARDED_FOR")
        }

        fun getFirstIp(request: HttpServletRequest): String? {
            var ip: String?
            for (header in HEADERS) {
                // 处理IP
                ip = handlerIp(request.getHeader(header))
                if (ip != null) {
                    return ip
                }
            }

            return handlerIp(request.remoteAddr)
        }

        /**
         * 处理IP, 可能是多个IP拼接, 处理成单个IP
         */
        fun handlerIp(originIp: String): String? {
            var originIp = originIp
            if (isLocalhost(originIp)) {
                return LOCALHOST
            }

            if (!StringUtils.hasText(originIp) || UNKNOWN.equals(originIp, ignoreCase = true)) {
                return null
            }

            if (originIp.contains(MULTI_IP_SPLIT)) {
                originIp = originIp.substring(0, originIp.indexOf(MULTI_IP_SPLIT))
            }

            if (originIp.length >= IPV4_LENGTH_MAX) {
                originIp = originIp.substring(0, IPV4_LENGTH_MAX)
            }

            return originIp
        }

        fun isLocalhost(ip: String?): Boolean {
            if (!StringUtils.hasText(ip)) {
                return false
            }
            return when (ip) {
                "[0:0:0:0:0:0:0:1]", "0:0:0:0:0:0:0:1", LOCALHOST, "localhost" -> true
                else -> false
            }
        }

        /**
         * 是否为正确的IP地址
         *
         * @param raw       原始值
         * @param predicate 附加判断,
         * @return true 满足是IP地址和附加判断时返回true
         */
        fun isIp(raw: String, predicate: Predicate<InetAddress>): Boolean {
            if (!StringUtils.hasText(raw)) {
                return false
            }
            try {
                val rawTrim: String = raw.trim { it <= ' ' }
                val rawNormalize: String = if (rawTrim.contains(IPV6_SPLIT))
                    rawTrim.replace("(^|:)0+(\\w+)".toRegex(), "$1$2")
                else
                    rawTrim.replace("(^|.)0+(\\w+)".toRegex(), "$1$2")

                val address = InetAddress.getByName(raw)
                val hostAddress = address.hostAddress
                // 解析出来的host地址与原始值一致则表示原始值为IP地址
                if (hostAddress != rawTrim && hostAddress != rawNormalize) {
                    return false
                }
                // 执行附加判断
                return predicate.test(address)
            } catch (e: Exception) {
                return false
            }
        }

        fun isIp(raw: String): Boolean {
            return isIp(raw) { i: InetAddress? -> true }
        }


        fun isIpv4(raw: String): Boolean {
            return isIp(raw) { address: InetAddress -> address.address.size == 4 }
        }


        fun isIpv6(raw: String): Boolean {
            return isIp(raw) { address: InetAddress -> address.address.size == 16 }
        }


        fun resolve(host: String?): String {
            val address = InetAddress.getByName(host)
            return address.hostAddress
        }

        fun list(request: HttpServletRequest): List<String> {
            val list: MutableList<String> = ArrayList()

            for (header in HEADERS) {
                val `val` = request.getHeader(header)
                if (StringUtils.hasText(`val`) && !UNKNOWN.equals(`val`, ignoreCase = true)) {
                    if (`val`.contains(MULTI_IP_SPLIT)) {
                        list.addAll(Arrays.asList<String>(*`val`.split(MULTI_IP_SPLIT.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()))
                    } else {
                        list.add(`val`)
                    }
                }
            }

            list.add(request.remoteAddr)
            return list
        }
    }
}
