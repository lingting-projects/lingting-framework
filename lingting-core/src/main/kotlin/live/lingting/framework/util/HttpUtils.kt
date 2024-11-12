package live.lingting.framework.util

import jakarta.servlet.http.HttpServletRequest

/**
 * @author lingting 2022/10/28 17:54
 */
class HttpUtils private constructor() {
    init {
        throw UnsupportedOperationException("This is a utility class and cannot be instantiated")
    }

    companion object {
        const val HEADER_HOST: String = "Host"

        const val HEADER_ORIGIN: String = "Origin"

        const val HEADER_USER_AGENT: String = "User-Agent"

        const val HEADER_AUTHORIZATION: String = "Authorization"

        const val HEADER_ACCEPT_LANGUAGE: String = "Accept-Language"

        fun host(request: HttpServletRequest): String {
            return request.getHeader(HEADER_HOST)
        }

        fun origin(request: HttpServletRequest): String {
            return request.getHeader(HEADER_ORIGIN)
        }

        fun language(request: HttpServletRequest): String {
            return request.getHeader(HEADER_ACCEPT_LANGUAGE)
        }

        fun authorization(request: HttpServletRequest): String {
            return request.getHeader(HEADER_AUTHORIZATION)
        }

        fun userAgent(request: HttpServletRequest): String {
            return request.getHeader(HEADER_USER_AGENT)
        }
    }
}
