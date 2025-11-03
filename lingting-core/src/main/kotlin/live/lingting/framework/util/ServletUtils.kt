package live.lingting.framework.util

import live.lingting.framework.http.HttpUrlBuilder
import live.lingting.framework.http.header.HttpHeaders
import live.lingting.framework.util.DurationUtils.days
import live.lingting.framework.util.DurationUtils.toSeconds
import live.lingting.framework.util.EnumerationUtils.forEach
import java.time.Duration
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * @author lingting 2025/8/29 17:22
 */
object ServletUtils {

    @JvmStatic
    fun headers(request: HttpServletRequest): HttpHeaders {
        return HttpHeaders.empty().let {
            request.headerNames.forEach { name ->
                val values = request.getHeaders(name)
                values.forEach { value ->
                    it.add(name, value)
                }
            }
            it.unmodifiable()
        }
    }

    @JvmStatic
    @JvmOverloads
    fun origin(request: HttpServletRequest, headers: HttpHeaders = headers(request)): String? {
        val real = headers.originReal()
        if (!real.isNullOrBlank()) {
            return real
        }
        val host = headers.hostReal()
        val builder = HttpUrlBuilder.builder()
            .apply {
                if (request.isSecure) https() else http()
            }
            .port(request.remotePort)
            // 优先用请求头的host
            .host(host ?: request.remoteHost)
        return "${builder.scheme()}://${builder.headerHost()}"
    }

    /**
     * 现有请求重定向到指定url. 保留当前请求方法
     * @param forever 是否永久重定向
     */
    @JvmStatic
    @JvmOverloads
    fun HttpServletResponse.redirect(forever: Boolean, url: String, expire: Duration? = null) {
        if (forever) {
            status = 308
        } else {
            status = 307
            if (expire != null) {
                setHeader("Cache-Control", "max-age=${expire.toSeconds()}")
            }
        }
        setHeader("Location", url)
    }

    /**
     * 现有请求重定向到指定url. 强制转为get请求
     * @param forever 是否永久重定向
     */
    @JvmStatic
    @JvmOverloads
    fun HttpServletResponse.redirectGet(forever: Boolean, url: String, expire: Duration = 1.days) {
        if (forever) {
            status = 301
        } else {
            status = 302
            setHeader("Cache-Control", "max-age=${expire.toSeconds()}")
        }
        setHeader("Location", url)
    }

}
