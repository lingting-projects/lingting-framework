package live.lingting.framework.util

import jakarta.servlet.http.HttpServletRequest
import java.util.regex.Pattern
import live.lingting.framework.http.header.HttpHeaders
import live.lingting.framework.kt.forEach

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
    fun isHttpUrl(string: String): Boolean {
        val matcher = PATTERN.matcher(string)
        return matcher.matches()
    }

}

