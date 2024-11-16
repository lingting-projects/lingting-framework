package live.lingting.framework.util

import jakarta.servlet.http.HttpServletRequest
import java.net.InetAddress
import java.util.Arrays
import java.util.function.Predicate

/**
 * @author psh 2022-04-21 16:55
 */
object IpUtils {

    const val LOCALHOST: String = "127.0.0.1"

    const val UNKNOWN: String = "UNKNOWN"

    const val MULTI_IP_SPLIT: String = ","

    const val IPV4_SPLIT: String = "."

    const val IPV6_SPLIT: String = ":"

    const val IPV4_LENGTH_MAX: Int = 16

    private val HEADERS: MutableList<String> = ArrayList(16)

    init {
        HEADERS.add("X-Forwarded-For")
        HEADERS.add("Node-Forwarded-IP")
        HEADERS.add("X-Real-Ip")
        HEADERS.add("Proxy-Client-IP")
        HEADERS.add("WL-Proxy-Client-IP")
        HEADERS.add("HTTP_CLIENT_IP")
        HEADERS.add("HTTP_X_FORWARDED_FOR")
    }

    @JvmStatic
    fun getFirstIp(request: HttpServletRequest): String {
        var ip: String

        for (header in HEADERS) {
            // 处理IP
            ip = handlerIp(request.getHeader(header))
            if (StringUtils.hasText(ip)) {
                return ip
            }
        }

        return handlerIp(request.remoteAddr)
    }

    /**
     * 处理IP, 可能是多个IP拼接, 处理成单个IP
     */
    @JvmStatic
    fun handlerIp(originIp: String): String {
        var originIp = originIp
        if (isLocalhost(originIp)) {
            return LOCALHOST
        }

        if (!StringUtils.hasText(originIp) || UNKNOWN.equals(originIp, ignoreCase = true)) {
            return ""
        }

        if (originIp.contains(MULTI_IP_SPLIT)) {
            originIp = originIp.substring(0, originIp.indexOf(MULTI_IP_SPLIT))
        }

        if (originIp.length >= IPV4_LENGTH_MAX) {
            originIp = originIp.substring(0, IPV4_LENGTH_MAX)
        }

        return originIp
    }

    @JvmStatic
    fun isLocalhost(ip: String): Boolean {
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
    @JvmStatic
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

    @JvmStatic
    fun isIp(raw: String): Boolean {
        return isIp(raw) { i: InetAddress -> true }
    }

    @JvmStatic
    fun isIpv4(raw: String): Boolean {
        return isIp(raw) { address: InetAddress -> address.address.size == 4 }
    }

    @JvmStatic
    fun isIpv6(raw: String): Boolean {
        return isIp(raw) { address: InetAddress -> address.address.size == 16 }
    }

    @JvmStatic
    fun resolve(host: String): String {
        val address = InetAddress.getByName(host)
        return address.hostAddress
    }

    @JvmStatic
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

