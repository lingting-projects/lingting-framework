package live.lingting.framework.http

import live.lingting.framework.util.CollectionUtils
import live.lingting.framework.util.StringUtils
import live.lingting.framework.value.MultiValue
import live.lingting.framework.value.multi.StringMultiValue
import java.net.URI
import java.net.URISyntaxException
import java.net.URL
import java.util.*
import java.util.function.BiConsumer

/**
 * @author lingting 2024-01-29 16:13
 */
class HttpUrlBuilder {
    protected val params: StringMultiValue = StringMultiValue()

    protected var scheme: String = "https"

    protected var host: String? = null

    protected var port: Int? = null

    protected var uri: StringBuilder = StringBuilder("/")

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

    fun uri(): String {
        return uri.toString()
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
        if (host.contains("://")) {
            val split: Array<String> = host.split("://".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            scheme(split[0])
            this.host = split[1]
        } else {
            this.host = host
        }
        return this
    }

    fun port(port: Int): HttpUrlBuilder {
        this.port = port
        return this
    }

    fun uri(string: String): HttpUrlBuilder {
        if (!StringUtils.hasText(string)) {
            this.uri = StringBuilder("/")
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
        this.uri = StringBuilder(newUri)
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

    fun uri(uri: StringBuilder): HttpUrlBuilder {
        return uri(uri.toString())
    }

    fun uriSegment(vararg segments: String): HttpUrlBuilder {
        if (!uri.isEmpty() && uri.substring(uri.length - 1) != "/") {
            uri.append("/")
        }

        for (segment in segments) {
            uri.append(segment).append("/")
        }

        return this
    }

    fun addParam(name: String, value: Any?): HttpUrlBuilder {
        params.ifAbsent(name)
        if (value is Map<*, *>) {
            value.forEach { (k: Any?, v: Any?) -> addParam(k.toString(), v) }
        } else if (CollectionUtils.isMulti(value)) {
            val list: List<Any> = CollectionUtils.multiToList(value)
            list.forEach { o: Any -> addParam(name, o) }
        } else if (value != null) {
            params.add(name, value.toString())
        }
        return this
    }

    fun addParams(params: Map<String, *>): HttpUrlBuilder {
        params.forEach { (name: String, value: Any?) -> this.addParam(name, value) }
        return this
    }

    fun addParams(params: MultiValue<String, *, *>): HttpUrlBuilder {
        params.forEach { name: Any?, value: Any? -> }
        params.forEach(object : BiConsumer<Any?, Any?> {
            override fun accept(t: Any?, u: Any?) {
                addParam(t.toString(), u)
            }
        })
        return this
    }

    fun build(): String {
        require(StringUtils.hasText(host)) { "Host [%s] is invalid!".formatted(host) }
        require(!(port != null && (port!! < 0 || port!! > 65535))) { "Port [%d] is invalid!".formatted(port) }

        val builder = StringBuilder()
        builder.append(scheme).append("://")
        builder.append(host)
        if (host.endsWith("/")) {
            builder.deleteCharAt(builder.length - 1)
        }
        if (port != null) {
            builder.append(":").append(port)
        }
        builder.append(buildPath())
        val query = buildQuery()
        if (StringUtils.hasText(query)) {
            if (builder[builder.length - 1] != '') {
                builder.append("")
            }
            builder.append(query)
        }
        return builder.toString()
    }

    fun buildPath(): String {
        if (!StringUtils.hasText(uri)) {
            return ""
        }
        val builder = StringBuilder()
        val string = uri.toString()
        if (!string.startsWith("/")) {
            builder.append("/")
        }
        builder.append(string)
        if (string.endsWith("/")) {
            builder.deleteCharAt(builder.length - 1)
        }
        return builder.toString()
    }

    fun buildUri(): URI {
        try {
            val path = buildPath()
            val query = buildQuery()
            val p = if (port == null) -1 else port!!
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
        return builder().scheme(scheme).host(host!!).port(port).uri(uri).addParams(params)
    }

    companion object {

        fun builder(): HttpUrlBuilder {
            return HttpUrlBuilder()
        }


        fun from(url: String): HttpUrlBuilder {
            val u = URI.create(url)
            return from(u)
        }


        fun from(u: URI): HttpUrlBuilder {
            val builder = builder().scheme(u.scheme).host(u.host).port(u.port).uri(u.path)
            val query = u.query
            if (StringUtils.hasText(query)) {
                Arrays.stream<String>(query.split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()).forEach { s: String ->
                    val split: Array<String> = s.split("=".toRegex(), limit = 2).toTypedArray()
                    val name = split[0]
                    builder.addParam(name, if (split.size == 1) null else split[1])
                }
            }
            return builder
        }


        fun buildQuery(value: MultiValue<String, String, *> = params): String {
            return buildQuery(value.map() as Map<String, Collection<String>>)
        }

        fun buildQuery(map: Map<String, Collection<String>>): String {
            if (CollectionUtils.isEmpty(map)) {
                return ""
            }
            val keys = map!!.keys.stream().sorted().toList()

            val builder = StringBuilder()
            for (key in keys) {
                val list = map[key]
                if (CollectionUtils.isEmpty(list)) {
                    builder.append(key).append("&")
                } else {
                    for (v in list!!) {
                        builder.append(key).append("=").append(v).append("&")
                    }
                }
            }

            return StringUtils.deleteLast(builder).toString()
        }
    }
}