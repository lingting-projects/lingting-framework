package live.lingting.framework.elasticsearch.composer

import co.elastic.clients.elasticsearch._types.FieldSort
import co.elastic.clients.elasticsearch._types.SortOptions
import co.elastic.clients.elasticsearch._types.SortOrder
import live.lingting.framework.elasticsearch.ElasticsearchFunction
import live.lingting.framework.elasticsearch.ElasticsearchUtils

/**
 * @author lingting 2024-03-06 17:44
 */
class SortComposer private constructor() {
    init {
        throw UnsupportedOperationException("This is a utility class and cannot be instantiated")
    }

    companion object {
        fun sort(field: String?, desc: Boolean): SortOptions {
            return sort(field, if (java.lang.Boolean.TRUE == desc) SortOrder.Desc else SortOrder.Asc)
        }

        fun sort(field: String?, order: SortOrder?): SortOptions {
            return SortOptions.of { so: SortOptions.Builder -> so.field { fs: FieldSort.Builder -> fs.field(field).order(order) } }
        }

        fun desc(field: String?): SortOptions {
            return sort(field, SortOrder.Desc)
        }

        fun asc(field: String?): SortOptions {
            return sort(field, SortOrder.Asc)
        }

        fun <E, T> sort(func: ElasticsearchFunction<E, T>, desc: Boolean): SortOptions {
            return sort(func, if (java.lang.Boolean.TRUE == desc) SortOrder.Desc else SortOrder.Asc)
        }

        fun <E, T> sort(func: ElasticsearchFunction<E, T>, order: SortOrder?): SortOptions {
            val field: String = ElasticsearchUtils.Companion.fieldName(func)
            return SortOptions.of { so: SortOptions.Builder -> so.field { fs: FieldSort.Builder -> fs.field(field).order(order) } }
        }

        fun <E, T> desc(func: ElasticsearchFunction<E, T>): SortOptions {
            return sort(func, SortOrder.Desc)
        }

        fun <E, T> asc(func: ElasticsearchFunction<E, T>): SortOptions {
            return sort(func, SortOrder.Asc)
        }
    }
}
