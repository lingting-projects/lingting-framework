package live.lingting.framework.util

import jakarta.servlet.http.HttpServletRequest
import java.util.regex.Pattern

/**
 * @author lingting 2022/10/28 17:54
 */
object HttpUtils {
    const val HEADER_HOST: String = "Host"

    const val HEADER_ORIGIN: String = "Origin"

    const val HEADER_USER_AGENT: String = "User-Agent"

    const val HEADER_AUTHORIZATION: String = "Authorization"

    const val HEADER_ACCEPT_LANGUAGE: String = "Accept-Language"

    @JvmField
    val PATTERN: Pattern = Pattern.compile("^https?://[a-zA-Z0-9.\\-]+(:[0-9]+)?(/.*)?\$")!!

    @JvmStatic
    fun host(request: HttpServletRequest): String {
        return request.getHeader(HEADER_HOST)
    }

    @JvmStatic
    fun origin(request: HttpServletRequest): String {
        return request.getHeader(HEADER_ORIGIN)
    }

    @JvmStatic
    fun language(request: HttpServletRequest): String {
        return request.getHeader(HEADER_ACCEPT_LANGUAGE)
    }

    @JvmStatic
    fun authorization(request: HttpServletRequest): String {
        return request.getHeader(HEADER_AUTHORIZATION)
    }

    @JvmStatic
    fun userAgent(request: HttpServletRequest): String {
        return request.getHeader(HEADER_USER_AGENT)
    }

    @JvmStatic
    fun isHttpUrl(string: String): Boolean {
        val matcher = PATTERN.matcher(string)
        return matcher.matches()
    }

}

