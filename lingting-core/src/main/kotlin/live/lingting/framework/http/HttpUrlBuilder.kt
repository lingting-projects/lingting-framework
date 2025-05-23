package live.lingting.framework.http

import live.lingting.framework.util.CollectionUtils
import live.lingting.framework.util.StringUtils
import live.lingting.framework.value.MultiValue
import live.lingting.framework.value.multi.StringMultiValue
import java.net.URI
import java.net.URISyntaxException
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

/**
 * @author lingting 2024-01-29 16:13
 */
open class HttpUrlBuilder {

    companion object {

        @JvmStatic
        fun builder(): HttpUrlBuilder {
            return HttpUrlBuilder()
        }

        @JvmStatic
        fun from(url: String): HttpUrlBuilder {
            val u = URI.create(url)
            return from(u)
        }

        @JvmStatic
        fun from(u: URI): HttpUrlBuilder {
            val builder = builder().scheme(u.scheme).host(u.host).port(u.port).path(u.path)
            val query = u.query
            if (StringUtils.hasText(query)) {
                query.split("&")
                    .dropLastWhile { it.isBlank() }
                    .forEach {
                        it.split("=", limit = 2)
                            .let { builder.addParam(it[0], if (it.size == 1) null else it[1]) }
                    }
            }
            return builder
        }

        @JvmStatic
        @JvmOverloads
        fun buildQuery(
            value: MultiValue<String, String, *>?,
            encode: Boolean = false,
            charset: Charset = StandardCharsets.UTF_8
        ): String {
            return buildQuery(value?.map(), encode, charset)
        }

        @JvmStatic
        @JvmOverloads
        fun buildQuery(
            map: Map<String, Collection<String>>?,
            encode: Boolean = false,
            charset: Charset = StandardCharsets.UTF_8
        ): String {
            if (map.isNullOrEmpty()) {
                return ""
            }
            val keys = map.keys.sorted().toList()

            val builder = StringBuilder()
            for (k in keys) {
                val vs = map[k]
                if (vs.isNullOrEmpty()) {
                    builder.append(k).append("&")
                } else {
                    vs.sorted().forEach { v ->
                        builder.append(k).append("=")
                        val av = if (encode) URLEncoder.encode(v, charset) else v
                        builder.append(av).append("&")
                    }
                }
            }

            return StringUtils.deleteLast(builder).toString()
        }
    }

    protected val params: StringMultiValue = StringMultiValue()

    protected var scheme: String = "https"

    protected var host: String? = null

    protected var port: Int? = null

    protected var path: StringBuilder = StringBuilder("/")

    fun params(): StringMultiValue {
        return params.unmodifiable()
    }

    fun scheme(): String {
        return scheme
    }

    fun host(): String? {
        return host
    }

    fun port(): Int? {
        return port
    }

    fun headerHost(): String {
        return port.let {
            if (it == null || it < 1) {
                "$host"
            } else {
                "$host:$it"
            }
        }
    }

    fun path(): String {
        return path.toString()
    }

    fun scheme(scheme: String): HttpUrlBuilder {
        this.scheme = scheme
        return this
    }

    fun http(): HttpUrlBuilder {
        return scheme("http")
    }

    fun https(): HttpUrlBuilder {
        return scheme("https")
    }

    fun host(host: String): HttpUrlBuilder {
        var v = host
        val schemeSplit = v.split("://", limit = 2)
        if (schemeSplit.size > 1) {
            scheme(schemeSplit[0])
            v = schemeSplit[1]
        }

        val portSplit = v.split(":", limit = 2)
        if (portSplit.size > 1) {
            port(portSplit[1].toInt())
            v = portSplit[0]
        }

        this.host = if (v.endsWith("/")) v.substringBeforeLast("/") else v
        return this
    }

    fun port(port: Int): HttpUrlBuilder {
        this.port = port
        return this
    }

    fun path(string: String): HttpUrlBuilder {
        if (!StringUtils.hasText(string)) {
            this.path = StringBuilder("/")
            return this
        }
        val newUri: String
        val query: String
        if (string.contains("?")) {
            val split: Array<String> = string.split("\\?".toRegex(), limit = 2).toTypedArray()
            newUri = split[0]
            query = split[1]
        } else {
            newUri = string
            query = ""
        }
        val builder = StringBuilder()
        if (!newUri.startsWith("/")) {
            builder.append("/")
        }
        builder.append(newUri)
        this.path = builder
        if (StringUtils.hasText(query)) {
            val split: Array<String> = query.split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (kv in split) {
                val array: Array<String> = kv.split("=".toRegex(), limit = 2).toTypedArray()
                val name = array[0]
                val value = if (array.size > 1) array[1] else null
                addParam(name, value)
            }
        }
        return this
    }

    fun path(path: StringBuilder): HttpUrlBuilder {
        return path(path.toString())
    }

    fun pathSegment(vararg segments: String): HttpUrlBuilder {
        if (path.isNotEmpty() && path.substring(path.length - 1) != "/") {
            path.append("/")
        }

        val builder = this.path
        for (segment in segments) {
            if (!builder.endsWith("/")) {
                builder.append("/")
            }
            builder.append(if (segment.startsWith("/")) segment.substring(1) else segment)
        }

        return this
    }

    fun addParam(name: String, value: Any?): HttpUrlBuilder {
        params.ifAbsent(name)
        when {
            value is Map<*, *> -> {
                value.forEach { (k: Any?, v: Any?) -> addParam(k.toString(), v) }
            }

            CollectionUtils.isMulti(value) -> {
                val list: List<Any?> = CollectionUtils.multiToList(value)
                list.forEach { addParam(name, it) }
            }

            value != null -> {
                params.add(name, value.toString())
            }
        }
        return this
    }

    fun addParams(params: Map<String, *>): HttpUrlBuilder {
        params.forEach { (name: String, value: Any?) -> this.addParam(name, value) }
        return this
    }

    fun addParams(params: MultiValue<String, *, *>): HttpUrlBuilder {
        params.forEach { name, value -> addParam(name, value) }
        return this
    }

    fun build(): String {
        require(StringUtils.hasText(host)) { "Host [$host] is invalid!" }
        val p = port
        require(p == null || (p > 0 && p < 65535)) { "Port [$p] is invalid!" }

        val builder = StringBuilder()
        builder.append(scheme).append("://")
        builder.append(host)
        if (host!!.endsWith("/")) {
            builder.deleteCharAt(builder.length - 1)
        }
        if (p != null) {
            builder.append(":").append(p)
        }
        builder.append(buildPath())
        val query = buildQuery()
        if (StringUtils.hasText(query)) {
            if (builder[builder.length - 1] != '?') {
                builder.append("?")
            }
            builder.append(query)
        }
        return builder.toString()
    }

    fun buildPath(): String {
        if (!StringUtils.hasText(path)) {
            return "/"
        }
        val builder = StringBuilder()
        val string = path.toString()
        if (!string.startsWith("/")) {
            builder.append("/")
        }
        builder.append(string)
        if (builder.length > 1 && string.endsWith("/")) {
            builder.deleteCharAt(builder.length - 1)
        }
        return builder.toString()
    }

    fun buildQuery(): String = buildQuery(params)

    fun buildUri(): URI {
        try {
            val path = buildPath()
            val query = buildQuery()
            val p = port.let { if (it == null || it < 1) -1 else it }
            val q = if (StringUtils.hasText(query)) query else null
            return URI(scheme, null, host, p, path, q, null)
        } catch (e: URISyntaxException) {
            throw IllegalStateException("Could not create URI object: " + e.message, e)
        }
    }

    fun buildUrl(): URL {
        return buildUri().toURL()
    }

    fun copy(): HttpUrlBuilder {
        val builder = builder().addParams(params).scheme(scheme).path(path)
        host?.let { builder.host(it) }
        port?.let { builder.port(it) }
        return builder
    }

}
