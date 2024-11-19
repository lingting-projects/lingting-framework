package live.lingting.framework.elasticsearch

import co.elastic.clients.elasticsearch.ElasticsearchClient
import co.elastic.clients.elasticsearch._types.Refresh
import co.elastic.clients.elasticsearch._types.Result
import co.elastic.clients.elasticsearch._types.Script
import co.elastic.clients.elasticsearch._types.SortOptions
import co.elastic.clients.elasticsearch._types.Time
import co.elastic.clients.elasticsearch._types.aggregations.Aggregate
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation
import co.elastic.clients.elasticsearch._types.query_dsl.Query
import co.elastic.clients.elasticsearch.core.BulkRequest
import co.elastic.clients.elasticsearch.core.BulkResponse
import co.elastic.clients.elasticsearch.core.ClearScrollRequest
import co.elastic.clients.elasticsearch.core.DeleteByQueryRequest
import co.elastic.clients.elasticsearch.core.GetRequest
import co.elastic.clients.elasticsearch.core.ScrollRequest
import co.elastic.clients.elasticsearch.core.SearchRequest
import co.elastic.clients.elasticsearch.core.SearchResponse
import co.elastic.clients.elasticsearch.core.UpdateByQueryRequest
import co.elastic.clients.elasticsearch.core.UpdateRequest
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem
import co.elastic.clients.elasticsearch.core.bulk.CreateOperation
import co.elastic.clients.elasticsearch.core.search.Hit
import co.elastic.clients.elasticsearch.core.search.HitsMetadata
import co.elastic.clients.elasticsearch.core.search.TotalHits
import co.elastic.clients.elasticsearch.core.search.TrackHits
import co.elastic.clients.util.ObjectBuilder
import java.io.IOException
import java.util.function.BiConsumer
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.UnaryOperator
import live.lingting.framework.api.LimitCursor
import live.lingting.framework.api.PaginationParams
import live.lingting.framework.api.PaginationResult
import live.lingting.framework.api.ScrollCursor
import live.lingting.framework.api.ScrollParams
import live.lingting.framework.api.ScrollResult
import live.lingting.framework.elasticsearch.builder.QueryBuilder
import live.lingting.framework.elasticsearch.composer.SortComposer
import live.lingting.framework.elasticsearch.datascope.ElasticsearchDataPermissionHandler
import live.lingting.framework.elasticsearch.datascope.ElasticsearchDataScope
import live.lingting.framework.function.ThrowingFunction
import live.lingting.framework.function.ThrowingRunnable
import live.lingting.framework.function.ThrowingSupplier
import live.lingting.framework.retry.Retry
import live.lingting.framework.util.CollectionUtils
import live.lingting.framework.util.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author lingting 2024-03-06 16:41
 */
class ElasticsearchApi<T>(
    val index: String, val cls: Class<T>, val idFunc: Function<T, String?>, properties: ElasticsearchProperties,
    val handler: ElasticsearchDataPermissionHandler?, val client: ElasticsearchClient
) {
    val retryProperties: ElasticsearchProperties.Retry? = properties.getRetry()

    val scrollSize: Long?

    val scrollTime: Time?

    constructor(
        cls: Class<T>, idFunc: Function<T, String?>, properties: ElasticsearchProperties,
        handler: ElasticsearchDataPermissionHandler?, client: ElasticsearchClient
    ) : this(ElasticsearchUtils.index(cls), cls, idFunc, properties, handler, client)

    init {
        var currentScrollSize: Long? = null
        var currentScrollTime: Time? = null

        val scroll = properties.getScroll()
        if (scroll != null) {
            currentScrollSize = scroll.size
            if (scroll.timeout != null) {
                currentScrollTime = Time.of { t: Time.Builder -> t.time("%ds".formatted(scroll.timeout.toSeconds())) }
            }
        }

        this.scrollSize = currentScrollSize
        this.scrollTime = currentScrollTime
    }

    fun documentId(t: T): String? {
        return idFunc.apply(t)
    }


    fun retry(runnable: ThrowingRunnable) {
        retry<Any>(ThrowingSupplier<Any> {
            runnable.run()
            null
        })
    }


    fun <R> retry(supplier: ThrowingSupplier<R>): R? {
        if (retryProperties == null || !retryProperties.isEnabled) {
            return supplier.get()
        }

        val retry: Retry<R> = ElasticsearchRetry(retryProperties, supplier)
        return retry.get()
    }

    fun merge(vararg arrays: Query): Query? {
        val builder: QueryBuilder<T> = QueryBuilder.builder<T>()
        Arrays.stream(arrays).filter { obj: Query? -> Objects.nonNull(obj) }.forEach { queries: Query? -> builder.addMust(queries) }
        return merge(builder)
    }

    fun merge(builder: QueryBuilder<T>): Query? {
        if (handler != null && !handler.ignorePermissionControl(index)) {
            handler.filterDataScopes(index).forEach(Consumer { scope: ElasticsearchDataScope? -> builder.addMust(scope!!.invoke(index)) })
        }

        return builder.build()
    }


    fun get(id: String?): T? {
        val request = GetRequest.of { gr: GetRequest.Builder -> gr.index(index).id(id) }
        return client.get(request, cls).source()
    }


    fun getByQuery(vararg queries: Query?): T? {
        return getByQuery(QueryBuilder.builder<T>(*queries))
    }


    fun getByQuery(queries: QueryBuilder<T>): T? {
        return getByQuery({ builder: SearchRequest.Builder -> builder }, queries)
    }


    fun getByQuery(operator: UnaryOperator<SearchRequest.Builder>, queries: QueryBuilder<T>): T? {
        return search({ builder: SearchRequest.Builder -> operator.apply(builder).size(1) }, queries).hits()
            .stream()
            .findFirst()
            .map { obj: Hit<T> -> obj.source() }
            .orElse(null)
    }


    fun count(vararg queries: Query?): Long {
        return count(QueryBuilder.builder<T>(*queries))
    }


    fun count(queries: QueryBuilder<T>): Long {
        val metadata = search({ builder: SearchRequest.Builder -> builder.size(0) }, queries)
        val hits = metadata.total()
        return hits?.value() ?: 0
    }


    fun search(vararg queries: Query?): HitsMetadata<T> {
        return search(QueryBuilder.builder<T>(*queries))
    }


    fun search(queries: QueryBuilder<T>): HitsMetadata<T> {
        return search({ builder: SearchRequest.Builder -> builder }, queries)
    }


    fun search(operator: UnaryOperator<SearchRequest.Builder>, queries: QueryBuilder<T>): HitsMetadata<T> {
        val query = merge(queries)

        val builder = operator.apply(
            SearchRequest.Builder() // 返回匹配的所有文档数量
                .trackTotalHits(TrackHits.of { th: TrackHits.Builder -> th.enabled(true) })

        )
        builder.index(index)
        builder.query(query)

        val searchResponse = client.search(builder.build(), cls)
        return searchResponse.hits()
    }

    fun ofLimitSort(sorts: Collection<PaginationParams.Sort?>): List<SortOptions> {
        if (CollectionUtils.isEmpty(sorts)) {
            return ArrayList()
        }
        return sorts.stream().map<SortOptions> { sort: PaginationParams.Sort? ->
            val field = StringUtils.underscoreToHump(sort!!.field)
            SortComposer.sort(field, sort.desc)
        }.toList()
    }


    fun page(params: PaginationParams, queries: QueryBuilder<T>): PaginationResult<T?> {
        val sorts = ofLimitSort(params.sorts)

        val from = params.start().toInt()
        val size = params.size.toInt()

        val hitsMetadata = search({ builder: SearchRequest.Builder -> builder.size(size).from(from).sort(sorts) }, queries)

        val list = hitsMetadata.hits().stream().map { obj: Hit<T> -> obj.source() }.toList()
        val total = Optional.ofNullable(hitsMetadata.total()).map { obj: TotalHits -> obj.value() }.orElse(0L)

        return PaginationResult(total, list)
    }


    fun aggs(
        consumer: BiConsumer<String?, Aggregate?>, aggregationMap: Map<String?, Aggregation?>?,
        queries: QueryBuilder<T>
    ) {
        aggs({ builder: SearchRequest.Builder -> builder }, consumer, aggregationMap, queries)
    }


    fun aggs(
        operator: UnaryOperator<SearchRequest.Builder>, consumer: BiConsumer<String?, Aggregate?>,
        aggregationMap: Map<String?, Aggregation?>?, queries: QueryBuilder<T>
    ) {
        aggs(operator, { response: SearchResponse<T> ->
            val aggregations = response.aggregations()
            val entries: Set<Map.Entry<String, Aggregate>> = aggregations.entries
            for ((key, aggregate) in entries) {
                consumer.accept(key, aggregate)
            }
        }, aggregationMap, queries)
    }


    fun aggs(
        operator: UnaryOperator<SearchRequest.Builder>, consumer: Consumer<SearchResponse<T>>,
        aggregationMap: Map<String?, Aggregation?>?, queries: QueryBuilder<T>
    ) {
        val query = merge(queries)

        val builder = operator.apply(
            SearchRequest.Builder() // 返回匹配的所有文档数量
                .trackTotalHits(TrackHits.of { th: TrackHits.Builder -> th.enabled(true) })

        )
        builder.size(0)
        builder.index(index)
        builder.query(query)
        builder.aggregations(aggregationMap)

        val response = client.search(builder.build(), cls)
        consumer.accept(response)
    }


    fun update(documentId: String?, scriptOperator: Function<Script.Builder?, ObjectBuilder<Script?>>): Boolean {
        return update(documentId, scriptOperator.apply(Script.Builder()).build())
    }


    fun update(documentId: String?, script: Script?): Boolean {
        return update({ builder: UpdateRequest.Builder<T, T?> -> builder }, documentId, script)
    }


    fun update(operator: UnaryOperator<UpdateRequest.Builder<T, T?>>, documentId: String?, script: Script?): Boolean {
        return update({ builder: UpdateRequest.Builder<T, T?> -> operator.apply(builder).script(script) }, documentId)
    }


    fun update(t: T): Boolean {
        return update({ builder: UpdateRequest.Builder<T, T?> -> builder.doc(t) }, documentId(t))
    }


    fun upsert(doc: T): Boolean {
        return update({ builder: UpdateRequest.Builder<T, T?> -> builder.doc(doc).docAsUpsert(true) }, documentId(doc))
    }


    fun upsert(doc: T, script: Script?): Boolean {
        return update({ builder: UpdateRequest.Builder<T, T?> -> builder.doc(doc).script(script) }, documentId(doc))
    }


    fun update(operator: UnaryOperator<UpdateRequest.Builder<T, T?>>, documentId: String?): Boolean {
        val builder = operator.apply(
            UpdateRequest.Builder<T, T?>() // 刷新策略
                .refresh(Refresh.WaitFor) // 版本冲突时自动重试次数
                .retryOnConflict(5)
        )

        builder.index(index).id(documentId)

        val response = client.update(builder.build(), cls)
        val result = response.result()
        return Result.Updated == result
    }


    fun updateByQuery(script: Script?, vararg queries: Query?): Boolean {
        return updateByQuery(script, QueryBuilder.builder<T>(*queries))
    }


    fun updateByQuery(
        scriptOperator: Function<Script.Builder?, ObjectBuilder<Script?>>,
        queries: QueryBuilder<T>
    ): Boolean {
        return updateByQuery(scriptOperator.apply(Script.Builder()).build(), queries)
    }


    fun updateByQuery(script: Script?, queries: QueryBuilder<T>): Boolean {
        return updateByQuery({ builder: UpdateByQueryRequest.Builder -> builder }, script, queries)
    }


    fun updateByQuery(
        operator: UnaryOperator<UpdateByQueryRequest.Builder>, script: Script?,
        queries: QueryBuilder<T>
    ): Boolean {
        val query = merge(queries)

        val builder = operator.apply(
            UpdateByQueryRequest.Builder() // 刷新策略
                .refresh(false)
        )
        builder.index(index).query(query).script(script)

        val response = client.updateByQuery(builder.build())
        val total = response.total()
        return total != null && total > 0
    }


    fun bulk(vararg operations: BulkOperation): BulkResponse {
        return bulk(Arrays.stream(operations).toList())
    }


    fun bulk(operations: List<BulkOperation>?): BulkResponse {
        return bulk({ builder: BulkRequest.Builder -> builder }, operations)
    }


    fun bulk(operator: UnaryOperator<BulkRequest.Builder>, operations: List<BulkOperation>?): BulkResponse {
        val builder = operator.apply(BulkRequest.Builder().refresh(Refresh.WaitFor))
        builder.index(index)
        builder.operations(operations)
        return client.bulk(builder.build())
    }


    fun save(t: T) {
        saveBatch(setOf(t))
    }


    fun saveBatch(collection: Collection<T>) {
        saveBatch({ builder: BulkRequest.Builder -> builder }, collection)
    }


    fun saveBatch(operator: UnaryOperator<BulkRequest.Builder>, collection: Collection<T>) {
        batch<T>(operator, collection, Function { t: T ->
            val documentId = documentId(t)
            val ob = BulkOperation.Builder()
            ob.create { create: CreateOperation.Builder<Any?> -> create.id(documentId).document(t) }
            ob.build()
        })
    }


    fun <E> batch(collection: Collection<E>, function: Function<E, BulkOperation>): BulkResponse {
        return batch<E>(UnaryOperator { builder: BulkRequest.Builder -> builder }, collection, function)
    }


    fun <E> batch(
        operator: UnaryOperator<BulkRequest.Builder>, collection: Collection<E?>,
        function: Function<E?, BulkOperation>
    ): BulkResponse {
        if (CollectionUtils.isEmpty(collection)) {
            return BulkResponse.of { br: BulkResponse.Builder -> br.errors(false).items(emptyList()).ingestTook(0L).took(0) }
        }

        val operations: MutableList<BulkOperation> = ArrayList()

        for (e in collection) {
            operations.add(function.apply(e))
        }

        val response = bulk({ builder: BulkRequest.Builder -> operator.apply(builder.refresh(Refresh.WaitFor)) }, operations)
        if (response.errors()) {
            val collect = response.items().stream().filter { item: BulkResponseItem -> item.error() != null }.toList()
            val allError = collect.size == collection.size
            for (i in (if (allError) 1 else 0) until collect.size) {
                val error = collect[i].error()
                log.warn("save error: {}", error)
            }

            // 全部保存失败, 抛异常
            if (allError) {
                throw IOException("bulk save error! " + collect[0].error())
            }
        }
        return response
    }


    fun deleteByQuery(vararg queries: Query?): Boolean {
        return deleteByQuery(QueryBuilder.builder<T>(*queries))
    }


    fun deleteByQuery(queries: QueryBuilder<T>): Boolean {
        return deleteByQuery({ builder: DeleteByQueryRequest.Builder -> builder }, queries)
    }


    fun deleteByQuery(operator: UnaryOperator<DeleteByQueryRequest.Builder>, queries: QueryBuilder<T>): Boolean {
        val query = merge(queries)

        val builder = operator.apply(DeleteByQueryRequest.Builder().refresh(false))
        builder.index(index)
        builder.query(query)

        val response = client.deleteByQuery(builder.build())
        val deleted = response.deleted()
        return deleted != null && deleted > 0
    }


    fun list(vararg queries: Query?): List<T?> {
        return list(QueryBuilder.builder<T>(*queries))
    }


    fun list(queries: QueryBuilder<T>): List<T?> {
        return list({ builder: SearchRequest.Builder -> builder }, queries)
    }


    fun list(operator: UnaryOperator<SearchRequest.Builder>, vararg queries: Query?): List<T?> {
        return list(operator, QueryBuilder.builder<T>(*queries))
    }


    fun list(operator: UnaryOperator<SearchRequest.Builder>, queries: QueryBuilder<T>): List<T?> {
        val list: MutableList<T?> = ArrayList()

        val params = ScrollParams<String?>(scrollSize!!, null)
        var records: List<T?>

        do {
            val result = scroll(operator, params, queries)
            records = result.records
            params.cursor = result.cursor

            if (!CollectionUtils.isEmpty(records)) {
                list.addAll(records)
            }
        } while (!CollectionUtils.isEmpty(records) && params.cursor != null)

        return list
    }


    fun scroll(params: ScrollParams<String?>, vararg queries: Query?): ScrollResult<T, String> {
        return scroll(params, QueryBuilder.builder<T>(*queries))
    }


    fun scroll(params: ScrollParams<String?>, queries: QueryBuilder<T>): ScrollResult<T, String> {
        return scroll({ builder: SearchRequest.Builder -> builder }, params, queries)
    }


    fun scroll(
        operator: UnaryOperator<SearchRequest.Builder>, params: ScrollParams<String?>,
        queries: QueryBuilder<T>
    ): ScrollResult<T, String> {
        var scrollId: String? = null
        if (params.cursor != null) {
            scrollId = params.cursor
        }
        // 非首次滚动查询, 直接使用 scrollId
        if (StringUtils.hasText(scrollId)) {
            return scroll({ builder: ScrollRequest.Builder -> builder.scroll(scrollTime) }, scrollId)
        }

        val query = merge(queries)
        val builder = operator.apply(
            SearchRequest.Builder().scroll(scrollTime) // 返回匹配的所有文档数量
                .trackTotalHits(TrackHits.of { th: TrackHits.Builder -> th.enabled(true) })
        ).index(index).query(query)

        if (params.size != null) {
            builder.size(params.size.intValue())
        }

        val search = client.search(builder.build(), cls)
        val collect = search.hits().hits().stream().map { obj: Hit<T> -> obj.source() }.filter { obj: T? -> Objects.nonNull(obj) }.toList()

        val nextScrollId = search.scrollId()

        // 如果首次滚动查询结果为空, 直接清除滚动上下文
        if (CollectionUtils.isEmpty(collect)) {
            clearScroll(nextScrollId)
        }

        return ScrollResult.of(collect, nextScrollId)
    }


    fun scroll(operator: UnaryOperator<ScrollRequest.Builder>, scrollId: String?): ScrollResult<T, String> {
        val builder = operator.apply(ScrollRequest.Builder()).scrollId(scrollId)

        val response = client.scroll(builder.build(), cls)
        val collect = response.hits().hits().stream().map { obj: Hit<T> -> obj.source() }.toList()
        val nextScrollId = response.scrollId()

        if (CollectionUtils.isEmpty(collect)) {
            clearScroll(nextScrollId)
            return ScrollResult.empty()
        }
        return ScrollResult.of(collect, nextScrollId)!!
    }


    fun clearScroll(scrollId: String?) {
        if (!StringUtils.hasText(scrollId)) {
            return
        }
        client.clearScroll { scr: ClearScrollRequest.Builder -> scr.scrollId(scrollId) }
    }

    fun pageCursor(params: PaginationParams, vararg queries: Query?): LimitCursor<T> {
        return pageCursor(params, QueryBuilder.builder<T>(*queries))
    }

    fun pageCursor(params: PaginationParams, queries: QueryBuilder<T>): LimitCursor<T> {
        return LimitCursor(ThrowingFunction<Long, PaginationResult<T>> { page: Long? ->
            params.page = page!!
            page(params, queries)
        })
    }


    fun scrollCursor(params: ScrollParams<String?>, vararg queries: Query?): ScrollCursor<T, String> {
        return scrollCursor(params, QueryBuilder.builder<T>(*queries))
    }


    fun scrollCursor(params: ScrollParams<String?>, queries: QueryBuilder<T>): ScrollCursor<T, String> {
        val scroll = scroll(params, queries)
        return ScrollCursor(ThrowingFunction<String, ScrollResult<T, String>> { scrollId: String? ->
            params.cursor = scrollId
            scroll(params, queries)
        }, scroll.cursor, scroll.records)
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(ElasticsearchApi::class.java)
    }
}
