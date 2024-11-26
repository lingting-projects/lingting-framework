package live.lingting.framework.elasticsearch.composer

import co.elastic.clients.elasticsearch._types.SortOptions
import co.elastic.clients.elasticsearch._types.SortOrder
import live.lingting.framework.elasticsearch.EFunction
import live.lingting.framework.elasticsearch.ElasticsearchUtils

/**
 * @author lingting 2024-03-06 17:44
 */
object SortComposer {
    @JvmStatic
    fun sort(field: String, desc: Boolean): SortOptions {
        return sort(field, if (java.lang.Boolean.TRUE == desc) SortOrder.Desc else SortOrder.Asc)
    }

    @JvmStatic
    fun sort(field: String, order: SortOrder): SortOptions {
        return SortOptions.of { so -> so.field { fs -> fs.field(field).order(order) } }
    }

    @JvmStatic
    fun desc(field: String): SortOptions {
        return sort(field, SortOrder.Desc)
    }

    @JvmStatic
    fun asc(field: String): SortOptions {
        return sort(field, SortOrder.Asc)
    }

    @JvmStatic
    fun <E, T> sort(func: EFunction<E, T>, desc: Boolean): SortOptions {
        return sort(func, if (java.lang.Boolean.TRUE == desc) SortOrder.Desc else SortOrder.Asc)
    }

    @JvmStatic
    fun <E, T> sort(func: EFunction<E, T>, order: SortOrder): SortOptions {
        val field: String = ElasticsearchUtils.fieldName(func)
        return SortOptions.of { so -> so.field { fs -> fs.field(field).order(order) } }
    }

    @JvmStatic
    fun <E, T> desc(func: EFunction<E, T>): SortOptions {
        return sort(func, SortOrder.Desc)
    }

    @JvmStatic
    fun <E, T> asc(func: EFunction<E, T>): SortOptions {
        return sort(func, SortOrder.Asc)
    }
}

