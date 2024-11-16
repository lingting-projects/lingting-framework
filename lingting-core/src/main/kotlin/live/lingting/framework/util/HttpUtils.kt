package live.lingting.framework.util

import jakarta.servlet.http.HttpServletRequest

/**
 * @author lingting 2022/10/28 17:54
 */
object HttpUtils {
    const val HEADER_HOST: String = "Host"

    const val HEADER_ORIGIN: String = "Origin"

    const val HEADER_USER_AGENT: String = "User-Agent"

    const val HEADER_AUTHORIZATION: String = "Authorization"

    const val HEADER_ACCEPT_LANGUAGE: String = "Accept-Language"

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
}

