package live.lingting.framework.elasticsearch

import live.lingting.framework.elasticsearch.annotation.Index
import live.lingting.framework.elasticsearch.polymerize.Polymerize
import live.lingting.framework.elasticsearch.polymerize.PolymerizeFactory
import live.lingting.framework.reflect.ClassField
import live.lingting.framework.util.AnnotationUtils
import live.lingting.framework.util.StringUtils

/**
 * @author lingting 2024/11/26 13:39
 */
data class IndexInfo(
    /**
     * 解析出来的基础索引, 数据范围和单个索引基于此
     */
    val index: String,
    /**
     * 匹配索引, 如果是多索引时默认使用此值进行查询, 前缀为 index. 单索引时与 index 一致
     */
    val matchIndex: String,
    /**
     * 数据原始类
     */
    val cls: Class<*>,
    /**
     * 分隔符
     */
    val separate: String,
    /**
     * 索引聚合策略
     */
    val polymerize: Polymerize,
    /**
     * 聚合参与的字段
     */
    val polymerizeFields: List<ClassField>,
    /**
     * 聚合索引查询限制
     */
    val polymerizeLimit: Long,
) {

    companion object {

        @JvmStatic
        fun create(properties: ElasticsearchProperties, cls: Class<*>, polymerizeFactory: PolymerizeFactory): IndexInfo {
            val a = AnnotationUtils.findAnnotation(cls, Index::class.java) ?: Index()
            val polymerize = polymerizeFactory.get(a.polymerize)
            val polymerizeFields = Polymerize.fields(cls)
            val polymerizeLimit = a.polymerizeLimit

            val config = properties.index
            val separate = config.separate
            val prefix = if (config.prefix.isNotBlank()) "${config.prefix}$separate${a.prefix}" else a.prefix
            val rawIndex = if (a.index.isBlank()) StringUtils.humpToUnderscore(cls.simpleName) else a.index
            val index = if (prefix.isNotBlank()) "$prefix$separate$rawIndex" else rawIndex
            val matchIndex = if (polymerizeFields.isNotEmpty()) "$index$separate*" else index
            return IndexInfo(index, matchIndex, cls, separate, polymerize, polymerizeFields, polymerizeLimit)
        }

    }

    fun index() = polymerize.index(this)

    fun <T> index(o: T) = polymerize.index(this, o)

    /**
     * 是否存在多个索引
     */
    val hasMulti = polymerizeFields.size > 1

}
