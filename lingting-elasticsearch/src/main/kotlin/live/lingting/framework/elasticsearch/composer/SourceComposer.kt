package live.lingting.framework.elasticsearch.composer

import co.elastic.clients.elasticsearch.core.search.SourceConfig
import live.lingting.framework.elasticsearch.EFunction
import live.lingting.framework.elasticsearch.ElasticsearchUtils

/**
 * @author lingting 2024-03-06 17:45
 */
object SourceComposer {
    @JvmStatic
    fun includes(value: String, vararg values: String): SourceConfig {
        return SourceConfig.of { sc -> sc.filter { sf -> sf.includes(value, *values) } }
    }

    @SafeVarargs
    @JvmStatic
    fun <E> includes(
        function: EFunction<E, *>,
        vararg functions: EFunction<E, *>
    ): SourceConfig {
        val value: String = ElasticsearchUtils.fieldName(function)
        val values = functions.map { ElasticsearchUtils.fieldName(it) }
        return includes(value, *values.toTypedArray())
    }

}
