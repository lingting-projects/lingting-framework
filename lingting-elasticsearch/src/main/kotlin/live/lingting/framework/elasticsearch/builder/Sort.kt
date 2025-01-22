package live.lingting.framework.elasticsearch.builder

import co.elastic.clients.elasticsearch._types.SortOptions
import co.elastic.clients.elasticsearch._types.SortOrder
import live.lingting.framework.elasticsearch.EFunction
import live.lingting.framework.elasticsearch.util.ElasticsearchUtils

/**
 * @author lingting 2025/1/21 15:45
 */
@Suppress("UNCHECKED_CAST")
interface Sort<E, B : Sort<E, B>> {

    fun merge(builder: Sort<*, *>): B {
        builder.buildSorts().forEach { sort(it) }
        return this as B
    }

    fun sort(options: SortOptions): B

    fun sort(field: String, desc: Boolean): B {
        val order = if (desc) SortOrder.Desc else SortOrder.Asc
        val options = SortOptions.of { it.field { it.field(field).order(order) } }
        return sort(options)
    }

    fun desc(field: String): B {
        return sort(field, true)
    }

    fun asc(field: String): B {
        return sort(field, false)
    }

    fun <E, T> sort(func: EFunction<E, T>, desc: Boolean): B {
        val field = ElasticsearchUtils.fieldName(func)
        return sort(field, desc)
    }

    fun <E, T> desc(func: EFunction<E, T>): B {
        return sort(func, true)
    }

    fun <E, T> asc(func: EFunction<E, T>): B {
        return sort(func, false)
    }

    fun buildSorts(): List<SortOptions>

}
