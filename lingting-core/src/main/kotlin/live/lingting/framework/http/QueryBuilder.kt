package live.lingting.framework.http

import live.lingting.framework.util.CollectionUtils
import live.lingting.framework.util.StringUtils
import live.lingting.framework.value.MultiValue
import live.lingting.framework.value.multi.StringMultiValue
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

/**
 * @author lingting 2025/5/28 15:59
 */
class QueryBuilder {

    constructor()

    constructor(map: Map<String, Any?>?) {
        val values = map?.mapValues { (_, v) -> CollectionUtils.multiToList(v).mapNotNull { it?.toString() } }
        values?.run { source.addAll(this) }
    }

    constructor(m: MultiValue<String, String, out Collection<String>>?) {
        m?.run { source.addAll(this) }
    }

    private val source = StringMultiValue()

    fun source() = source.unmodifiable()

    var encode = true

    var charset: Charset = StandardCharsets.UTF_8

    var sort = false

    /**
     * 自定义排序
     */
    var comparator: Comparator<String>? = null

    /**
     * 空值名称添加 =
     */
    var emptyValueEqual = false

    /**
     * 是否在名称后面添加索引作为后缀
     */
    var indexSuffix = false

    /**
     * 分隔符
     */
    var indexSuffixSeparation = "."

    /**
     * 是否给空值添加后缀
     */
    var indexSuffixEmpty = false

    /**
     * 是否给所有的名称均添加后缀
     */
    var indexSuffixAll = true

    /**
     * 当 indexSuffixAll=false 时. 仅在这个set中找到的名称进去后缀添加
     */
    var indexMatchNames = setOf<String>()

    /**
     * index 起始值
     */
    var indexStart = 1

    fun add(name: String, v: String) = source.add(name, v)

    fun addAll(map: Map<String, Any?>?) {
        val values = map?.mapValues { (_, v) -> CollectionUtils.multiToList(v).mapNotNull { it?.toString() } }
        values?.run { source.addAll(this) }
    }

    fun addAll(m: MultiValue<String, String, Collection<String>>?) {
        m?.run { source.addAll(this) }
    }

    fun put(name: String, v: String) = source.put(name, v)

    fun putAll(map: Map<String, Any?>?) {
        val values = map?.mapValues { (_, v) -> CollectionUtils.multiToList(v).mapNotNull { it?.toString() } }
        values?.run { source.putAll(this) }
    }

    fun putAll(m: MultiValue<String, String, Collection<String>>?) {
        m?.run { source.putAll(this) }
    }

    fun build(): String {
        if (source.isEmpty) {
            return ""
        }

        val unmodifiable = source.unmodifiable()
        return buildString {
            val keys = sort(unmodifiable.keys())
            for (k in keys) {
                val vs = sort(unmodifiable.get(k))
                val name = if (encode) HttpUrlBuilder.encode(k, charset) else k

                if (vs.isEmpty()) {
                    append(name)
                    if (indexSuffix && indexSuffixEmpty) {
                        append(indexSuffixSeparation).append(indexStart)
                    }
                    if (emptyValueEqual) {
                        append("=")
                    }
                    append("&")
                } else {
                    vs.forEachIndexed { i, v ->
                        append(name)
                        if (indexSuffix && indexSuffixAll || indexMatchNames.contains(k)) {
                            append(indexSuffixSeparation).append(indexStart + i)
                        }

                        val value = if (encode) HttpUrlBuilder.encode(v, charset) else v
                        append("=").append(value).append("&")
                    }
                }
            }

            if (endsWith("&")) {
                StringUtils.deleteLast(this)
            }
        }
    }

    private fun sort(c: Collection<String>?): List<String> {
        if (c == null) {
            return emptyList()
        }
        if (!sort) {
            return c.toList()
        }
        val r = comparator
        if (r == null) {
            return c.sorted()
        }
        return c.sortedWith(r)
    }

}
