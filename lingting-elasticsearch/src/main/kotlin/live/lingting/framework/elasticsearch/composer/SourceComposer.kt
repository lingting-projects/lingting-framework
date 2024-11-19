package live.lingting.framework.elasticsearch.composer

import co.elastic.clients.elasticsearch.core.search.SourceConfig
import co.elastic.clients.elasticsearch.core.search.SourceFilter
import live.lingting.framework.elasticsearch.ElasticsearchFunction
import live.lingting.framework.elasticsearch.ElasticsearchUtils


/**
 * @author lingting 2024-03-06 17:45
 */
class SourceComposer private constructor() {
    init {
        throw UnsupportedOperationException("This is a utility class and cannot be instantiated")
    }

    companion object {
        fun includes(value: String?, vararg values: String?): SourceConfig {
            return SourceConfig.of { sc: SourceConfig.Builder -> sc.filter { sf: SourceFilter.Builder -> sf.includes(value, *values) } }
        }

        @SafeVarargs
        fun <E> includes(
            function: ElasticsearchFunction<E, *>,
            vararg functions: ElasticsearchFunction<E, *>
        ): SourceConfig {
            val value: String = ElasticsearchUtils.fieldName(function)
            val values = Arrays.stream<ElasticsearchFunction<E, *>>(functions).map<String> { func: ElasticsearchFunction<E, *> -> ElasticsearchUtils.fieldName(func) }.toArray<String> { _Dummy_.__Array__() }
            return includes(value, *values)
        }
    }
}
