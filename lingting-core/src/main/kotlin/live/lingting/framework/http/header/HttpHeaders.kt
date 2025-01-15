package live.lingting.framework.http.header

import live.lingting.framework.http.header.HttpHeaderKeys.ACCEPT_LANGUAGE
import live.lingting.framework.http.header.HttpHeaderKeys.AUTHORIZATION
import live.lingting.framework.http.header.HttpHeaderKeys.CONTENT_LENGTH
import live.lingting.framework.http.header.HttpHeaderKeys.CONTENT_TYPE
import live.lingting.framework.http.header.HttpHeaderKeys.ETAG
import live.lingting.framework.http.header.HttpHeaderKeys.HOST
import live.lingting.framework.http.header.HttpHeaderKeys.ORIGIN
import live.lingting.framework.http.header.HttpHeaderKeys.RANGE
import live.lingting.framework.http.header.HttpHeaderKeys.USER_AGENT
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
    fun host(): String? {
        return first(HOST)
    }

    fun origin(): String? {
        return first(ORIGIN)
    }

    fun language(): String? {
        return first(ACCEPT_LANGUAGE)
    }

    fun ua(): String? {
        return first(USER_AGENT)
    }

    fun authorization(): String? {
        return first(AUTHORIZATION)
    }

    fun contentType(): String? {
        return first(CONTENT_TYPE)
    }

    fun contentLength(): Long {
        val first = first(CONTENT_LENGTH, "0")
        return first.toLong()
    }

    fun etag(): String? {
        return first(ETAG)
    }

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

    fun contentLength(contentLength: Long): HttpHeaders {
        put(CONTENT_LENGTH, contentLength.toString())
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

}
