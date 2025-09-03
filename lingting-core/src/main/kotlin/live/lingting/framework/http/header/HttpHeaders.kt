package live.lingting.framework.http.header

import live.lingting.framework.http.HttpUrlBuilder
import live.lingting.framework.http.header.HttpHeaderKeys.ACCEPT_LANGUAGE
import live.lingting.framework.http.header.HttpHeaderKeys.AUTHORIZATION
import live.lingting.framework.http.header.HttpHeaderKeys.CONTENT_LENGTH
import live.lingting.framework.http.header.HttpHeaderKeys.CONTENT_TYPE
import live.lingting.framework.http.header.HttpHeaderKeys.ETAG
import live.lingting.framework.http.header.HttpHeaderKeys.FORWARDED_BY
import live.lingting.framework.http.header.HttpHeaderKeys.FORWARDED_FOR
import live.lingting.framework.http.header.HttpHeaderKeys.FORWARDED_HOST
import live.lingting.framework.http.header.HttpHeaderKeys.FORWARDED_PROTO
import live.lingting.framework.http.header.HttpHeaderKeys.HOST
import live.lingting.framework.http.header.HttpHeaderKeys.ORIGIN
import live.lingting.framework.http.header.HttpHeaderKeys.RANGE
import live.lingting.framework.http.header.HttpHeaderKeys.REFERER
import live.lingting.framework.http.header.HttpHeaderKeys.USER_AGENT
import live.lingting.framework.http.header.HttpHeaderKeys.X_FORWARDED_FOR
import live.lingting.framework.http.header.HttpHeaderKeys.X_FORWARDED_HOST
import live.lingting.framework.http.header.HttpHeaderKeys.X_FORWARDED_PORT
import live.lingting.framework.http.header.HttpHeaderKeys.X_FORWARDED_PROTO
import live.lingting.framework.http.header.HttpHeaderKeys.X_FORWARDED_SERVER
import live.lingting.framework.util.StringUtils
import live.lingting.framework.value.MultiValue

/**
 * @author lingting 2024-09-12 23:38
 */
interface HttpHeaders : MultiValue<String, String, MutableCollection<String>> {

    companion object {

        @JvmStatic
        @JvmOverloads
        fun empty(allowModify: Boolean = true): HttpHeaders {
            return CollectionHttpHeaders(allowModify)
        }

        @JvmStatic
        fun of(value: MultiValue<String, String, out Collection<String>>): HttpHeaders {
            return of(value.map())
        }

        @JvmStatic
        fun of(map: Map<String, Collection<String>>): HttpHeaders {
            val empty = empty()
            empty.addAll(map)
            return empty
        }

    }

    override fun unmodifiable(): UnmodifiableHttpHeaders

    // region get
    fun host(): String? = first(HOST)

    fun origin(): String? = first(ORIGIN)

    fun referer(): String? = first(REFERER)

    fun language(): String? = first(ACCEPT_LANGUAGE)

    fun ua(): String? = first(USER_AGENT)

    fun authorization(): String? = first(AUTHORIZATION)

    fun contentType(): String? = first(CONTENT_TYPE)

    fun contentLength(): Long = first(CONTENT_LENGTH, "0").toLong()

    fun etag(): String? = first(ETAG)

    fun charset(): String? {
        return contentType()?.lowercase()?.let {
            val text = "charset="
            if (it.contains(text)) {
                it.substring(it.indexOf(text) + text.length + 1)
            } else {
                null
            }
        }
    }

    fun xForwardedFor(): String? = first(X_FORWARDED_FOR)
    fun xForwardedProto(): String? = first(X_FORWARDED_PROTO)
    fun xForwardedPort(): String? = first(X_FORWARDED_PORT)
    fun xForwardedHost(): String? = first(X_FORWARDED_HOST)
    fun xForwardedServer(): String? = first(X_FORWARDED_SERVER)

    fun forwardedFor(): String? = first(FORWARDED_FOR)
    fun forwardedProto(): String? = first(FORWARDED_PROTO)
    fun forwardedHost(): String? = first(FORWARDED_HOST)
    fun forwardedBy(): String? = first(FORWARDED_BY)

    // endregion

    // region set
    fun host(host: String): HttpHeaders {
        put(HOST, host)
        return this
    }

    fun authorization(authorization: String): HttpHeaders {
        put(AUTHORIZATION, authorization)
        return this
    }

    fun contentType(contentType: String?): HttpHeaders {
        if (StringUtils.hasText(contentType)) {
            put(CONTENT_TYPE, contentType!!)
        } else {
            remove(CONTENT_TYPE)
        }
        return this
    }

    fun contentLength(contentLength: Number): HttpHeaders {
        put(CONTENT_LENGTH, contentLength.toLong().toString())
        return this
    }

    fun etag(etag: String): HttpHeaders {
        put(ETAG, etag)
        return this
    }

    fun range(start: Long, end: Long): HttpHeaders {
        put(RANGE, String.format("bytes=%d-%d", start, end))
        return this
    }

    // endregion

    // region 计算行为

    fun originByForwarded(): String? {
        val proto = forwardedProto()
        if (proto.isNullOrBlank()) {
            return null
        }
        val host = forwardedHost()
        if (host.isNullOrBlank()) {
            return null
        }
        return "$proto://$host"
    }

    fun originByXForwarded(): String? {
        val proto = xForwardedProto()
        if (proto.isNullOrBlank()) {
            return null
        }
        val host = xForwardedHost()
        if (host.isNullOrBlank()) {
            return null
        }
        return "$proto://$host"
    }

    fun originByReferer(): String? {
        val referer = referer()
        if (referer.isNullOrBlank()) {
            return null
        }
        val builder = HttpUrlBuilder.from(referer)
        return "${builder.scheme()}://${builder.headerHost()}"
    }

    /**
     * 尝试获取真实的源
     */
    fun originReal(): String? {
        val forwarded = originByForwarded()
        if (!forwarded.isNullOrBlank()) {
            return forwarded
        }
        val xForwarded = originByXForwarded()
        if (!xForwarded.isNullOrBlank()) {
            return xForwarded
        }
        val referer = originByReferer()
        return referer ?: origin()
    }

    fun hostReal(): String? {
        val forwarded = forwardedHost()
        if (!forwarded.isNullOrBlank()) {
            return forwarded
        }
        val xForwarded = xForwardedHost()
        if (!xForwarded.isNullOrBlank()) {
            return xForwarded
        }
        return host()
    }

    // endregion

}
