package live.lingting.framework.elasticsearch.builder

import co.elastic.clients.elasticsearch._types.SortOptions
import co.elastic.clients.elasticsearch._types.query_dsl.Query
import co.elastic.clients.elasticsearch.core.SearchRequest
import co.elastic.clients.elasticsearch.core.search.SourceConfig
import co.elastic.clients.elasticsearch.core.search.TrackHits
import java.util.function.Consumer

/**
 * @author lingting 2024-06-17 17:06
 */
open class SearchBuilder<E> @JvmOverloads constructor(
    protected val compare: Compare<E, *> = DefaultCompare<E>(),
    protected val sortBuilder: Sort<E, *> = DefaultSort<E>(),
    protected val sourceBuilder: Source<E, *> = DefaultSource<E>(),
) : Builder<SearchRequest.Builder, SearchBuilder<E>>,
    Compare<E, SearchBuilder<E>>,
    Sort<E, SearchBuilder<E>>,
    Source<E, SearchBuilder<E>> {

    protected var all = true

    fun all(all: Boolean): SearchBuilder<E> {
        this.all = all
        return this
    }

    // region query

    override fun merge(builder: Compare<*, *>): SearchBuilder<E> {
        compare.merge(builder)
        return this
    }

    override fun addMust(queries: Collection<Query>): SearchBuilder<E> {
        compare.addMust(queries)
        return this
    }

    override fun addMustNot(queries: Collection<Query>): SearchBuilder<E> {
        compare.addMustNot(queries)
        return this
    }

    override fun addShould(queries: Collection<Query>): SearchBuilder<E> {
        compare.addShould(queries)
        return this
    }

    override fun buildQuery(): Query = compare.buildQuery()

    // endregion

    // region sort

    override fun merge(builder: Sort<*, *>): SearchBuilder<E> {
        sortBuilder.merge(builder)
        return this
    }

    override fun sort(options: SortOptions): SearchBuilder<E> {
        sortBuilder.sort(options)
        return this
    }

    override fun buildSorts(): List<SortOptions> {
        return sortBuilder.buildSorts()
    }

    // endregion

    // region source
    override fun source(source: SourceConfig?): SearchBuilder<E> {
        sourceBuilder.source(source)
        return this
    }

    override fun buildSource(): SourceConfig? {
        return sourceBuilder.buildSource()
    }

    // endregion

    protected var customizer: Consumer<SearchRequest.Builder>? = null

    override fun customizer(consumer: Consumer<SearchRequest.Builder>): SearchBuilder<E> {
        this.customizer = consumer
        return this
    }

    override fun copy(): SearchBuilder<E> {
        return SearchBuilder<E>().merge(this)
    }

    override fun merge(builder: SearchBuilder<E>): SearchBuilder<E> {
        compare.merge(builder.compare)
        sortBuilder.merge(builder.sortBuilder)
        sourceBuilder.source(builder.buildSource())
        return this
    }

    override fun build(): SearchRequest.Builder {
        val builder = SearchRequest.Builder()
        builder.query(buildQuery())
        builder.sort(buildSorts())
        if (all) {
            builder.trackTotalHits(TrackHits.of { th -> th.enabled(true) })
        }
        customizer?.accept(builder)
        return builder
    }

}
