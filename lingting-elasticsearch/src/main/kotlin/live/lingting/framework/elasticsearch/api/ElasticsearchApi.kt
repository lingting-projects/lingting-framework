package live.lingting.framework.elasticsearch.api

import co.elastic.clients.elasticsearch._types.Script
import co.elastic.clients.elasticsearch._types.Time
import co.elastic.clients.elasticsearch._types.aggregations.Aggregate
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation
import co.elastic.clients.elasticsearch._types.query_dsl.Query
import co.elastic.clients.elasticsearch.core.BulkRequest
import co.elastic.clients.elasticsearch.core.BulkResponse
import co.elastic.clients.elasticsearch.core.DeleteByQueryRequest
import co.elastic.clients.elasticsearch.core.DeleteByQueryResponse
import co.elastic.clients.elasticsearch.core.ScrollRequest
import co.elastic.clients.elasticsearch.core.SearchRequest
import co.elastic.clients.elasticsearch.core.SearchResponse
import co.elastic.clients.elasticsearch.core.UpdateByQueryRequest
import co.elastic.clients.elasticsearch.core.UpdateRequest
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation
import co.elastic.clients.elasticsearch.core.bulk.BulkOperationBase
import co.elastic.clients.elasticsearch.core.bulk.CreateOperation
import co.elastic.clients.elasticsearch.core.bulk.DeleteOperation
import co.elastic.clients.elasticsearch.core.bulk.IndexOperation
import co.elastic.clients.elasticsearch.core.bulk.UpdateOperation
import java.io.IOException
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.UnaryOperator
import live.lingting.framework.api.PaginationCursor
import live.lingting.framework.api.PaginationParams
import live.lingting.framework.api.PaginationResult
import live.lingting.framework.api.ScrollCursor
import live.lingting.framework.api.ScrollParams
import live.lingting.framework.api.ScrollResult
import live.lingting.framework.elasticsearch.aggregate.AggregationWrapper
import live.lingting.framework.elasticsearch.builder.AggBuilder
import live.lingting.framework.elasticsearch.builder.Compare
import live.lingting.framework.elasticsearch.builder.ScriptBuilder
import live.lingting.framework.elasticsearch.builder.SearchBuilder
import live.lingting.framework.elasticsearch.builder.UpdateBuilder
import live.lingting.framework.elasticsearch.retry.ElasticsearchRetryProperties
import live.lingting.framework.elasticsearch.util.ElasticsearchUtils.toOptions
import live.lingting.framework.function.ThrowingSupplier
import live.lingting.framework.util.StringUtils
import org.slf4j.Logger

/**
 * @author lingting 2025/1/22 11:36
 */
interface ElasticsearchApi<T> {

    val log: Logger

    val scrollSize: Long

    val scrollTime: Time?

    // region basic

    fun documentId(t: T): String? = null

    fun index(): Collection<String>

    fun index(t: T): String

    fun <R> retry(supplier: ThrowingSupplier<R>): R?

    fun <R> retry(properties: ElasticsearchRetryProperties?, supplier: ThrowingSupplier<R>): R?

    fun merge(builder: Compare<T, *>)

    fun search(): SearchBuilder<T> = SearchBuilder()

    fun aggs(): AggBuilder<T> = AggBuilder()

    fun script(): ScriptBuilder<T> = ScriptBuilder()

    fun update(): UpdateBuilder<T> = UpdateBuilder()

    // endregion

    // region query

    fun getByDoc(documentId: String): T?

    fun get(vararg queries: Query) = get(queries.toList())

    fun get(queries: Collection<Query>) = get(search().addMust(queries))

    fun get(builder: SearchBuilder<T>) = get({ it }, builder)

    fun get(operator: UnaryOperator<SearchRequest.Builder>, builder: SearchBuilder<T>): T? {
        return search(builder) { operator.apply(it).size(1) }.hits().hits().firstOrNull()?.source()
    }

    fun count(vararg queries: Query) = count(queries.toList())

    fun count(queries: Collection<Query>) = count(search().addMust(queries))

    fun count(builder: SearchBuilder<T>) = count({ it }, builder)

    fun count(operator: UnaryOperator<SearchRequest.Builder>, builder: SearchBuilder<T>): Long {
        return search(builder) { operator.apply(it).size(0) }.hits().total()?.value() ?: 0
    }

    fun page(params: PaginationParams, vararg queries: Query) = page(params, queries.toList())

    fun page(params: PaginationParams, queries: Collection<Query>) = page(params, search().addMust(queries))

    fun page(params: PaginationParams, builder: SearchBuilder<T>): PaginationResult<T> {
        val sorts = params.toOptions()
        val from = params.start().toInt()
        val size = params.size.toInt()

        val meta = search(builder) { it.size(size).from(from).sort(sorts) }.hits()

        val list = meta.hits().mapNotNull { obj -> obj.source() }
        val total = meta.total()?.value() ?: 0

        return PaginationResult<T>(total, list)
    }

    fun list(vararg queries: Query) = list(queries.toList())

    fun list(queries: Collection<Query>) = list(search().addMust(queries))

    fun list(builder: SearchBuilder<T>) = list({ it }, builder)

    fun list(operator: UnaryOperator<ScrollRequest.Builder>, builder: SearchBuilder<T>): List<T> {
        return scrollCursor(ScrollParams(scrollSize), builder, operator).toList()
    }

    fun search(vararg queries: Query) = search(queries.toList())

    fun search(queries: Collection<Query>) = search(search().addMust(queries))

    fun search(builder: SearchBuilder<T>) = search(builder) { it }

    fun search(builder: SearchBuilder<T>, operator: UnaryOperator<SearchRequest.Builder>): SearchResponse<T>

    // endregion

    // region scroll

    fun scroll(vararg queries: Query) = scroll(queries.toList())

    fun scroll(queries: Collection<Query>) = scroll(search().addMust(queries))

    fun scroll(builder: SearchBuilder<T>) = scroll(ScrollParams(scrollSize), builder) { it }

    fun scroll(params: ScrollParams<String>, vararg queries: Query) = scroll(params, queries.toList())

    fun scroll(params: ScrollParams<String>, queries: Collection<Query>) = scroll(params, search().addMust(queries))

    fun scroll(params: ScrollParams<String>, builder: SearchBuilder<T>) = scroll(params, builder) { it }

    fun scroll(params: ScrollParams<String>, builder: SearchBuilder<T>, operator: UnaryOperator<ScrollRequest.Builder>): ScrollResult<T, String> {
        var scrollId: String? = null
        if (params.cursor != null) {
            scrollId = params.cursor
        }
        // 非首次滚动查询, 直接使用 scrollId
        if (StringUtils.hasText(scrollId)) {
            return scroll(scrollId, operator)
        }

        val response = search(builder) { it.scroll(scrollTime).size(params.size.toInt()) }
        val collect = response.hits().hits().mapNotNull { it.source() }
        val nextScrollId = response.scrollId()

        // 如果首次滚动查询结果为空, 直接清除滚动上下文
        if (collect.isEmpty()) {
            scrollClear(nextScrollId)
        }

        return ScrollResult.Companion.of<T, String>(collect, nextScrollId)
    }

    fun scroll(scrollId: String?, operator: UnaryOperator<ScrollRequest.Builder>): ScrollResult<T, String>

    fun scrollClear(scrollId: String?)

    // endregion

    // region aggregate

    fun aggs(agg: Aggregation, consumer: Consumer<Aggregate>) {
        val wrapper = AggregationWrapper("agg", agg, consumer)
        aggs(wrapper)
    }

    fun aggs(vararg wrappers: AggregationWrapper) = aggs(wrappers.toList())

    fun aggs(wrappers: Collection<AggregationWrapper>) = aggs(AggBuilder<T>().aggs(wrappers))

    fun aggs(builder: AggBuilder<T>) = aggs({ it }, builder)

    fun aggs(operator: UnaryOperator<SearchRequest.Builder>, builder: AggBuilder<T>) {
        val response = aggs(operator, builder.buildAggregations(), builder.buildSearch())
        for (entry in response.aggregations()) {
            val key = entry.key
            val aggregate = entry.value
            builder.consumer(key, aggregate)
        }
    }

    fun aggs(operator: UnaryOperator<SearchRequest.Builder>, aggregationMap: Map<String, Aggregation>, builder: SearchBuilder<T>): SearchResponse<T> {
        return search(builder) {
            operator.apply(it).aggregations(aggregationMap).size(0)
        }
    }

    // endregion

    // region cursor

    fun pageCursor(params: PaginationParams, vararg queries: Query) = pageCursor(params, queries.toList())

    fun pageCursor(params: PaginationParams, queries: Collection<Query>) = pageCursor(params, search().addMust(queries))

    fun pageCursor(params: PaginationParams, queries: SearchBuilder<T>): PaginationCursor<T> {
        return PaginationCursor(params) { page(it, queries) }
    }

    fun scrollCursor(params: ScrollParams<String>, vararg queries: Query) = scrollCursor(params, queries.toList())

    fun scrollCursor(params: ScrollParams<String>, queries: Collection<Query>) = scrollCursor(params, search().addMust(queries))

    fun scrollCursor(params: ScrollParams<String>, queries: SearchBuilder<T>) = scrollCursor(params, queries, UnaryOperator { it })

    fun scrollCursor(params: ScrollParams<String>, queries: SearchBuilder<T>, operator: UnaryOperator<ScrollRequest.Builder>): ScrollCursor<T, String> {
        return ScrollCursor(params) { scroll(it, queries, operator) }
    }


    // endregion

    // region update

    fun update(t: T) = update({ it.doc(t).index(index(t)) }, documentId(t))

    fun upsert(t: T) = update({ it.upsert(t).index(index(t)) }, documentId(t))

    fun upsert(t: T, script: Script) = update({ it.doc(t).upsert(t).script(script).index(index(t)) }, documentId(t))

    fun update(documentId: String?, script: ScriptBuilder<T>) = update(documentId, script.build())

    fun update(documentId: String?, script: Script) = update({ it.script(script) }, documentId)

    fun update(operator: UnaryOperator<UpdateRequest.Builder<T, T>>, documentId: String?): Boolean

    // endregion

    // region updateByQuery

    fun updateByQuery(script: ScriptBuilder<T>, vararg queries: Query) = updateByQuery(script, queries.toList())

    fun updateByQuery(script: ScriptBuilder<T>, queries: Collection<Query>) = updateByQuery(script.build(), queries)

    fun updateByQuery(script: Script, vararg queries: Query) = updateByQuery(script, queries.toList())

    fun updateByQuery(script: Script, queries: Collection<Query>) = updateByQuery(script, update().addMust(queries))

    fun updateByQuery(script: Script, queries: UpdateBuilder<T>) = updateByQuery(queries) { it.script(script) }

    fun updateByQuery(update: UpdateBuilder<T>): Long = updateByQuery(update) { it }

    fun updateByQuery(builder: UpdateBuilder<T>, operator: UnaryOperator<UpdateByQueryRequest.Builder>): Long

    // endregion

    // region delete

    fun deleteByQuery(vararg queries: Query) = deleteByQuery(queries.toList())

    fun deleteByQuery(queries: Collection<Query>) = deleteByQuery(search().addMust(queries))

    fun deleteByQuery(builder: SearchBuilder<T>) = deleteByQuery(builder) { it }.deleted() ?: 0

    fun deleteByQuery(builder: SearchBuilder<T>, operator: UnaryOperator<DeleteByQueryRequest.Builder>): DeleteByQueryResponse

    // endregion

    // region bulk

    fun bulk(vararg collection: T, convert: Function<T, BulkOperationBase.AbstractBuilder<*>>) = bulk(collection.toList(), convert)

    fun bulk(collection: List<T>, convert: Function<T, BulkOperationBase.AbstractBuilder<*>>) = bulk(collection, convert) { it }

    fun bulk(collection: Collection<T>, convert: Function<T, BulkOperationBase.AbstractBuilder<*>>, operator: UnaryOperator<BulkRequest.Builder>): BulkResponse {
        val operations = collection.map {
            val builder = BulkOperation.Builder()
            val apply = convert.apply(it)
            val index = index(it)
            apply.index(index)
            when (apply) {
                is UpdateOperation.Builder<*, *> -> builder.update(apply.build())
                is CreateOperation.Builder<*> -> builder.create(apply.build())
                is DeleteOperation.Builder -> builder.delete(apply.build())
                else -> builder.index((apply as IndexOperation.Builder<*>).build())
            }.build()
        }
        return bulk(operations, operator)
    }

    fun bulk(vararg operations: BulkOperation) = bulk(operations.toList())

    fun bulk(operations: List<BulkOperation>) = bulk(operations) { it }

    fun bulk(operations: List<BulkOperation>, operator: UnaryOperator<BulkRequest.Builder>): BulkResponse

    // endregion

    // region batch

    fun batchThrow(collection: Collection<T>, response: BulkResponse): BulkResponse {
        if (response.errors()) {
            val collect = response.items().filter { item -> item.error() != null }
            val allError = collect.size == collection.size
            for (i in (if (allError) 1 else 0) until collect.size) {
                val error = collect[i].error()
                log.warn("batch error: {}", error)
            }

            // 全部保存失败, 抛异常
            if (allError) {
                throw IOException("bulk error! " + collect[0].error())
            }
        }
        return response
    }

    fun batch(vararg collection: T, convert: Function<T, BulkOperationBase.AbstractBuilder<*>>): BulkResponse = batch(collection.toList(), convert)

    fun batch(collection: List<T>, convert: Function<T, BulkOperationBase.AbstractBuilder<*>>) = batch(collection, convert) { it }

    fun batch(collection: Collection<T>, convert: Function<T, BulkOperationBase.AbstractBuilder<*>>, operator: UnaryOperator<BulkRequest.Builder>): BulkResponse {
        val response = bulk(collection, convert, operator)
        return batchThrow(collection, response)
    }

    // endregion

    // region save

    fun save(t: T) = saveBatch(t) > 0

    fun saveBatch(vararg collection: T) = saveBatch(collection.toList())

    fun saveBatch(collection: Collection<T>) = saveBatch(collection) { it }

    fun saveBatch(collection: Collection<T>, operator: UnaryOperator<BulkRequest.Builder>): Int {
        val response = batch(collection, {
            val documentId = documentId(it)
            CreateOperation.Builder<T>().id(documentId).document(it).index(index(it))
        }, operator)
        val errorCount = response.items().count { it.error() != null }
        return collection.size - errorCount
    }

    // endregion

}
