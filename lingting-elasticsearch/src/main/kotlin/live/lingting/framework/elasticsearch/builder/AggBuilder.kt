package live.lingting.framework.elasticsearch.builder

import co.elastic.clients.elasticsearch._types.SortOptions
import co.elastic.clients.elasticsearch._types.aggregations.Aggregate
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation
import co.elastic.clients.elasticsearch._types.query_dsl.Query
import co.elastic.clients.elasticsearch.core.SearchRequest
import co.elastic.clients.elasticsearch.core.search.SourceConfig
import java.util.function.Consumer
import live.lingting.framework.elasticsearch.aggregate.AggregationWrapper

/**
 * @author lingting 2025/1/22 11:18
 */
class AggBuilder<E> @JvmOverloads constructor(
    private var searchBuilder: SearchBuilder<E> = SearchBuilder<E>(),
) : Builder<SearchRequest.Builder, AggBuilder<E>>,
    Compare<E, AggBuilder<E>>,
    Sort<E, AggBuilder<E>>,
    Source<E, AggBuilder<E>> {

    fun all(all: Boolean): AggBuilder<E> {
        searchBuilder.all(all)
        return this
    }

    fun merge(builder: SearchBuilder<E>): AggBuilder<E> {
        searchBuilder.merge(builder)
        return this
    }

    private val aggs = LinkedHashSet<AggregationWrapper>()

    @JvmOverloads
    fun agg(key: String, aggregation: Aggregation, on: Consumer<Aggregate>? = null): AggBuilder<E> {
        val wrapper = AggregationWrapper(key, aggregation, on)
        return agg(wrapper)
    }

    fun agg(wrapper: AggregationWrapper): AggBuilder<E> {
        aggs.add(wrapper)
        return this
    }

    fun aggs(wrappers: Collection<AggregationWrapper>): AggBuilder<E> {
        aggs.addAll(wrappers)
        return this
    }

    fun consumer(key: String, aggregate: Aggregate) {
        aggs.forEach {
            if (it.key == key) {
                it.on?.accept(aggregate)
            }
        }
    }

    fun buildAggregations(): Map<String, Aggregation> {
        val map = mutableMapOf<String, Aggregation>()
        aggs.forEach {
            map[it.key] = it.value
        }
        return map
    }

    fun buildSearch(): SearchBuilder<E> = searchBuilder.copy()

    // region query

    override fun merge(builder: Compare<*, *>): AggBuilder<E> {
        searchBuilder.merge(builder)
        return this
    }

    override fun addMust(queries: Collection<Query>): AggBuilder<E> {
        searchBuilder.addMust(queries)
        return this
    }

    override fun addMustNot(queries: Collection<Query>): AggBuilder<E> {
        searchBuilder.addMustNot(queries)
        return this
    }

    override fun addShould(queries: Collection<Query>): AggBuilder<E> {
        searchBuilder.addShould(queries)
        return this
    }

    override fun buildQuery(): Query = searchBuilder.buildQuery()

    // endregion

    // region sort

    override fun merge(builder: Sort<*, *>): AggBuilder<E> {
        searchBuilder.merge(builder)
        return this
    }

    override fun sort(options: SortOptions): AggBuilder<E> {
        searchBuilder.sort(options)
        return this
    }

    override fun buildSorts(): List<SortOptions> {
        return searchBuilder.buildSorts()
    }

    // endregion

    // region source
    override fun source(source: SourceConfig?): AggBuilder<E> {
        searchBuilder.source(source)
        return this
    }

    override fun buildSource(): SourceConfig? {
        return searchBuilder.buildSource()
    }

    // endregion

    private var customizer: Consumer<SearchRequest.Builder>? = null

    override fun customizer(consumer: Consumer<SearchRequest.Builder>): AggBuilder<E> {
        this.customizer = consumer
        return this
    }

    override fun copy(): AggBuilder<E> {
        return AggBuilder<E>().merge(this)
    }

    override fun merge(builder: AggBuilder<E>): AggBuilder<E> {
        searchBuilder.merge(builder.searchBuilder)
        return this
    }

    override fun build(): SearchRequest.Builder {
        val builder = searchBuilder.build()
        aggs.forEach {
            builder.aggregations(it.key, it.value)
        }
        customizer?.accept(builder)
        return builder
    }

}
