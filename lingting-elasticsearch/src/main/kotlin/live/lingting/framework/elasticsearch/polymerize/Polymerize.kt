package live.lingting.framework.elasticsearch.polymerize

import live.lingting.framework.elasticsearch.IndexInfo
import live.lingting.framework.elasticsearch.annotation.IndexPolymerize
import live.lingting.framework.reflect.ClassField
import live.lingting.framework.util.ClassUtils

/**
 * @author lingting 2024/11/26 13:39
 */
interface Polymerize {

    companion object {
        @JvmStatic
        fun fields(cls: Class<*>): List<ClassField> {
            return ClassUtils.classFields(cls).filter { it.hasAnnotation(IndexPolymerize::class.java) }
        }
    }

    /**
     * 查询时使用的索引
     */
    fun index(info: IndexInfo): String = if (info.polymerizeLimit < 1) info.matchIndex else indices(info).joinToString(",")

    fun indices(info: IndexInfo): LinkedHashSet<String>

    /**
     * 获取指定实体对象的聚合索引
     */
    fun <T> index(info: IndexInfo, o: T): String

}
