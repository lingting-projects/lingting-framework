package live.lingting.framework.elasticsearch.builder

import co.elastic.clients.elasticsearch._types.SortOptions

/**
 * @author lingting 2025/1/21 15:52
 */
open class DefaultSort<E> : Sort<E, DefaultSort<E>> {

    protected val sorts = LinkedHashSet<SortOptions>()

    override fun merge(builder: Sort<*, *>): DefaultSort<E> {
        if (builder is DefaultSort) {
            sorts.addAll(builder.sorts)
        } else {
            sorts.addAll(builder.buildSorts())
        }
        return this
    }

    override fun sort(options: SortOptions): DefaultSort<E> {
        sorts.add(options)
        return this
    }

    override fun buildSorts(): List<SortOptions> = sorts.toList()

}
