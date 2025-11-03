package live.lingting.framework.http

import live.lingting.framework.util.CollectionUtils
import live.lingting.framework.util.StringUtils
import live.lingting.framework.value.MultiValue
import live.lingting.framework.value.multi.StringMultiValue
import java.net.URI
import java.net.URISyntaxException
import java.net.URL
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.regex.Pattern

/**
 * 存入此对象的值全都是 decode 之后的值. 包括 路径, 参数名称和参数值 等.
 * @author lingting 2024-01-29 16:13
 */
open class HttpUrlBuilder {

    companion object {

        @JvmStatic
        fun builder(): HttpUrlBuilder {
            return HttpUrlBuilder()
        }

        @JvmStatic
        @JvmOverloads
        fun encode(s: String, charset: Charset = StandardCharsets.UTF_8): String {
            val encode = URLEncoder.encode(s, charset.name())
            return encode.replace("%25", "%")
        }

        @JvmStatic
        @JvmOverloads
        fun decode(s: String, charset: Charset = StandardCharsets.UTF_8): String {
            val l = s.replace("%25", "%")
            return URLDecoder.decode(l, charset.name())
        }

        const val URL_PATTERN_REGEX: String =
            // 协议部分
            "^(https?)://" +
                    // Host部分
                    "(([a-zA-Z0-9.\\-]+)(:[0-9]+)?)" +
                    // path 部分
                    "(/?[^?]*)?" +
                    // query 部分
                    "(\\??.*)?" +
                    "$"

        @JvmField
        val URL_PATTERN: Pattern = Pattern.compile(URL_PATTERN_REGEX)!!

        /**
         * @param decode 1: 传入参数可正常转为 URI 时(编码后的url 或者 未编码但是不携带特殊符号的url). 是否对转换后的值进行二次解码. 2: 传入参数不可正常转为URI时, 手动解析后是否进行解码
         */
        @JvmStatic
        @JvmOverloads
        fun from(url: String, decode: Boolean = false, charset: Charset = StandardCharsets.UTF_8): HttpUrlBuilder {
            return try {
                // 默认 url 可直接转换.
                val u = URI.create(url)
                from(u, decode, charset)
            } catch (_: IllegalArgumentException) {
                // url参数未编码 且 存在特殊字符.  直接解析
                val matcher = URL_PATTERN.matcher(url)
                if (!matcher.matches()) {
                    throw IllegalArgumentException("invalid url: $url")
                }
                val builder = builder()
                    .scheme(matcher.group(1))
                    .host(matcher.group(2))
                val count = matcher.groupCount()
                if (count > 4) {
                    val path = matcher.group(5)
                    builder.path(path)
                }
                if (count > 5) {
                    val query = matcher.group(6)
                    builder.addParamsByQuery(query, decode, charset)
                }

                builder
            }
        }

        @JvmStatic
        @JvmOverloads
        fun from(u: URI, decode: Boolean = false, charset: Charset = StandardCharsets.UTF_8): HttpUrlBuilder {
            val builder = builder().scheme(u.scheme).host(u.host).port(u.port).path(u.path)
            var query = u.query
            var fragment = u.fragment
            // fragment 处理现在仅兼容 hash路由
            if (!fragment.isNullOrBlank()) {
                fragment = "#$fragment"
                // 优先追加到path
                if (u.path.isNullOrBlank()) {
                    builder.path(fragment)
                }
                // 然后追加到query
                else if (!query.isNullOrBlank()) {
                    query = "${query}&${fragment}"
                } else {
                    query = fragment
                }
            }
            return builder.addParamsByQuery(query, decode, charset)
        }

        @JvmStatic
        @JvmOverloads
        fun buildQuery(
            value: MultiValue<String, String, *>?,
            encode: Boolean = true,
            charset: Charset = StandardCharsets.UTF_8,
            sort: Boolean = false
        ): String {
            return buildQuery(value?.map(), encode, charset, sort)
        }

        @JvmStatic
        @JvmOverloads
        fun buildQuery(
            map: Map<String, Collection<String>>?,
            encode: Boolean = true,
            charset: Charset = StandardCharsets.UTF_8,
            sort: Boolean = false
        ): String {
            return QueryBuilder(map).also {
                it.encode = encode
                it.charset = charset
                it.sort = sort
            }.build()
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

    fun isHttps() = scheme == "https"

    fun host(): String? {
        return host
    }

    fun port(): Int? {
        return port
    }

    fun headerHost(): String {
        val h = host
        require(!h.isNullOrBlank()) { "Host [$host] is invalid!" }
        val p = port
        require(p == null || (p > 0 && p < 65535)) { "Port [$p] is invalid!" }

        if (port == null) {
            return h
        }
        val https = isHttps()
        if (https && port == 443) {
            return h
        } else if (!https && port == 80) {
            return h
        }

        return "$h:$p"
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
        this.port = if (port < 0) null else port
        return this
    }

    fun path(string: String?): HttpUrlBuilder {
        if (string.isNullOrBlank()) {
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
        return addParamsByQuery(query)
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

    fun addParams(params: Map<String, Any>): HttpUrlBuilder {
        params.forEach { (name: String, value: Any?) -> this.addParam(name, value) }
        return this
    }

    fun addParams(params: MultiValue<String, out Any, out Collection<Any>>): HttpUrlBuilder {
        params.forEach { name, value -> addParam(name, value) }
        return this
    }

    @JvmOverloads
    fun addParamsByQuery(
        query: String?,
        decode: Boolean = false,
        charset: Charset = StandardCharsets.UTF_8
    ): HttpUrlBuilder {
        if (query.isNullOrBlank()) {
            return this
        }
        val query = query.trim().substringAfter("?")
        val split = query.split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (kv in split) {
            val array: Array<String> = kv.split("=".toRegex(), limit = 2).toTypedArray()
            val n = array[0]
            val v = if (array.size > 1) array[1] else null
            val name = if (decode) decode(n, charset) else n
            val value = if (decode && v != null) decode(v, charset) else v
            addParam(name, value)
        }
        return this
    }

    fun clearParams(): HttpUrlBuilder {
        params.clear()
        return this
    }

    @JvmOverloads
    fun removeParam(name: String, value: String? = null): HttpUrlBuilder {
        if (value == null) {
            params.remove(name)
        } else {
            params.remove(name, value)
        }
        return this
    }

    @JvmOverloads
    fun removeAllParams(name: String, collection: Collection<String>? = null): HttpUrlBuilder {
        if (collection == null) {
            params.remove(name)
        } else if (collection.isNotEmpty()) {
            collection.forEach { c ->
                params.remove(c)
            }
        }
        return this
    }

    @JvmOverloads
    fun build(encode: Boolean = true, charset: Charset = StandardCharsets.UTF_8): String {
        val builder = StringBuilder()
        builder.append(scheme).append("://")
        val host = headerHost()
        builder.append(host)
        builder.append(buildPath())
        val query = buildQuery(encode, charset)
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

    @JvmOverloads
    fun buildQuery(encode: Boolean = true, charset: Charset = StandardCharsets.UTF_8): String =
        buildQuery(params, encode, charset)

    fun buildUri(): URI {
        try {
            val url = build()
            return URI.create(url)
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
