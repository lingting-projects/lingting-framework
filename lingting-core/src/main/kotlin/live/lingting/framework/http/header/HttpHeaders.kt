package live.lingting.framework.http.header

import live.lingting.framework.util.HttpUtils
import live.lingting.framework.util.StringUtils
import live.lingting.framework.value.MultiValue

/**
 * @author lingting 2024-09-12 23:38
 */
interface HttpHeaders : MultiValue<String, String, MutableCollection<String>> {

    override fun unmodifiable(): UnmodifiableHttpHeaders

    // region get
    fun host(): String? {
        return first(HttpUtils.HEADER_HOST)
    }

    fun origin(): String? {
        return first(HttpUtils.HEADER_ORIGIN)
    }

    fun language(): String? {
        return first(HttpUtils.HEADER_ACCEPT_LANGUAGE)
    }

    fun ua(): String? {
        return first(HttpUtils.HEADER_USER_AGENT)
    }

    fun authorization(): String? {
        return first(HttpUtils.HEADER_AUTHORIZATION)
    }

    fun contentType(): String? {
        return first("Content-Type")
    }

    fun contentLength(): Long {
        val first = first("Content-Length", "0")
        return first.toLong()
    }

    fun etag(): String? {
        return first("ETag")
    }

    // endregion

    // region set
    fun host(host: String): HttpHeaders {
        put(HttpUtils.HEADER_HOST, host)
        return this
    }

    fun authorization(authorization: String): HttpHeaders {
        put(HttpUtils.HEADER_AUTHORIZATION, authorization)
        return this
    }

    fun contentType(contentType: String?): HttpHeaders {
        if (StringUtils.hasText(contentType)) {
            put("Content-Type", contentType!!)
        } else {
            remove("Content-Type")
        }
        return this
    }

    fun contentLength(contentLength: Long): HttpHeaders {
        put("Content-Length", contentLength.toString())
        return this
    }

    fun etag(etag: String): HttpHeaders {
        put("ETag", etag)
        return this
    }

    fun range(start: Long, end: Long): HttpHeaders {
        put("Range", String.format("bytes=%d-%d", start, end))
        return this
    } // endregion

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
}
