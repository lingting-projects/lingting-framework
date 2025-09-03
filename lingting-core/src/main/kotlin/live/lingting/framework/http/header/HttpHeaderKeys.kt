package live.lingting.framework.http.header

/**
 * @author lingting 2025/1/14 19:50
 */
object HttpHeaderKeys {

    const val CONTENT_TYPE = "Content-Type"

    const val CONTENT_LENGTH = "Content-Length"

    const val ETAG = "ETag"

    const val HOST = "Host"

    const val AUTHORIZATION = "Authorization"

    const val RANGE = "Range"

    const val ORIGIN = "Origin"

    const val REFERER = "referer"

    const val USER_AGENT = "User-Agent"

    const val ACCEPT_LANGUAGE = "Accept-Language"

    const val ACCEPT_ENCODING = "Accept-Encoding"

    const val ACCEPT_CHARSET = "Accept-Charset"

    const val CONNECTION = "Connection"

    const val CONTENT_ENCODING = "Content-Encoding"

    const val CACHE_CONTROL = "Cache-Control"

    const val PRAGMA = "Pragma"

    const val COOKIE = "Cookie"

    const val LOCATION = "Location"

    const val SET_COOKIE = "Set-Cookie"

    const val IF_MODIFIED_SINCE = "If-Modified-Since"

    const val IF_NONE_MATCH = "If-None-Match"

    const val IF_RANGE = "If-Range"

    const val IF_UNMODIFIED_SINCE = "If-Unmodified-Since"

    const val PROXY_AUTHORIZATION = "Proxy-Authorization"

    const val PROXY_CONNECTION = "Proxy-Connection"

    const val TE = "TE"

    const val UPGRADE = "Upgrade"

    const val VIA = "Via"

    /**
     * 常用 但是非标准 传递客户端真实 IP 及代理 IP 链
     */
    const val X_FORWARDED_FOR = "X-Forwarded-For"

    /**
     * 常用 但是非标准 传递原始通信协议（http/https）
     */
    const val X_FORWARDED_PROTO = "X-Forwarded-Proto"

    /**
     * 常用 但是非标准 传递原始访问端口
     */
    const val X_FORWARDED_PORT = "X-Forwarded-Port"

    /**
     * 常用 但是非标准 传递原始访问域名
     */
    const val X_FORWARDED_HOST = "X-Forwarded-Host"

    /**
     * 常用 但是非标准 传递转发代理的 IP / 主机名
     */
    const val X_FORWARDED_SERVER = "X-Forwarded-Server"

    /**
     * 不常用 但是标准 传递客户端真实 IP 及代理 IP 链
     */
    const val FORWARDED_FOR = "FORWARDED-FOR"

    /**
     * 不常用 但是标准 传递原始通信协议（http/https）
     */
    const val FORWARDED_PROTO = "FORWARDED-PROTO"

    /**
     * 不常用 但是标准 传递原始访问域名
     */
    const val FORWARDED_HOST = "FORWARDED-HOST"

    /**
     * 不常用 但是标准 传递转发代理的 IP / 主机名
     */
    const val FORWARDED_BY = "FORWARDED-BY"

}

