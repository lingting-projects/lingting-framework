package live.lingting.framework.elasticsearch.api

import co.elastic.clients.elasticsearch.ElasticsearchClient
import co.elastic.clients.elasticsearch._types.Refresh
import co.elastic.clients.elasticsearch._types.Result
import co.elastic.clients.elasticsearch._types.Time
import co.elastic.clients.elasticsearch.core.BulkRequest
import co.elastic.clients.elasticsearch.core.BulkResponse
import co.elastic.clients.elasticsearch.core.DeleteByQueryRequest
import co.elastic.clients.elasticsearch.core.DeleteByQueryResponse
import co.elastic.clients.elasticsearch.core.GetRequest
import co.elastic.clients.elasticsearch.core.ScrollRequest
import co.elastic.clients.elasticsearch.core.SearchRequest
import co.elastic.clients.elasticsearch.core.SearchResponse
import co.elastic.clients.elasticsearch.core.UpdateByQueryRequest
import co.elastic.clients.elasticsearch.core.UpdateRequest
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation
import java.util.function.Function
import java.util.function.UnaryOperator
import live.lingting.framework.api.ScrollResult
import live.lingting.framework.elasticsearch.ElasticsearchProperties
import live.lingting.framework.elasticsearch.IndexInfo
import live.lingting.framework.elasticsearch.builder.Compare
import live.lingting.framework.elasticsearch.builder.SearchBuilder
import live.lingting.framework.elasticsearch.builder.UpdateBuilder
import live.lingting.framework.elasticsearch.interceptor.Interceptor
import live.lingting.framework.elasticsearch.polymerize.PolymerizeFactory
import live.lingting.framework.elasticsearch.retry.ElasticsearchRetry
import live.lingting.framework.elasticsearch.retry.ElasticsearchRetryProperties
import live.lingting.framework.function.ThrowingSupplier
import live.lingting.framework.util.Slf4jUtils.logger
import live.lingting.framework.util.StringUtils
import org.slf4j.Logger

/**
 * @author lingting 2024-03-06 16:41
 */
class ElasticsearchApiImpl<T>(
    val info: IndexInfo,
    val cls: Class<T>,
    val getDocumentId: Function<T, String?>?,
    val properties: ElasticsearchProperties,
    val interceptors: List<Interceptor>,
    val client: ElasticsearchClient
) : ElasticsearchApi<T> {

    companion object {

        private val log = logger()

    }

    constructor(
        cls: Class<T>,
        polymerizeFactory: PolymerizeFactory,
        getDocumentId: Function<T, String?>?,
        properties: ElasticsearchProperties,
        interceptors: List<Interceptor>,
        client: ElasticsearchClient
    ) : this(
        IndexInfo.create(properties, cls, polymerizeFactory),
        cls, getDocumentId, properties, interceptors, client
    )

    override val log: Logger = Companion.log

    override val scrollSize: Long = properties.scroll.size

    override val scrollTime: Time = Time.of { t -> t.time("${properties.scroll.timeout.toSeconds()}s") }

    override fun documentId(t: T): String? = getDocumentId?.apply(t)

    override fun index(): List<String> = info.index()

    override fun index(t: T): String = info.index(t)

    override fun <R> retry(supplier: ThrowingSupplier<R>): R? {
        return retry(ElasticsearchRetryProperties.from(properties), supplier)
    }

    override fun <R> retry(properties: ElasticsearchRetryProperties?, supplier: ThrowingSupplier<R>): R? {
        if (properties == null) {
            return supplier.get()
        }
        val retry = ElasticsearchRetry<R>(properties, supplier)
        return retry.get()
    }

    override fun merge(builder: Compare<T, *>) {
        interceptors.forEach {
            it.intercept(info, builder)
        }
    }

    override fun getByDoc(documentId: String): T? {
        val request = GetRequest.of { it.index(info.index().joinToString(",")).id(documentId) }
        return client[request, cls].source()
    }

    override fun search(builder: SearchBuilder<T>, operator: UnaryOperator<SearchRequest.Builder>): SearchResponse<T> {
        merge(builder)
        val request = operator.apply(builder.build())
            .index(index())
            .build()
        return client.search(request, cls)
    }

    override fun scroll(scrollId: String?, operator: UnaryOperator<ScrollRequest.Builder>): ScrollResult<T, String> {
        val builder = operator.apply(ScrollRequest.Builder().scroll(scrollTime)).scrollId(scrollId)

        val response = client.scroll(builder.build(), cls)
        val collect = response.hits().hits().mapNotNull { it.source() }
        val nextScrollId = response.scrollId()

        if (collect.isEmpty()) {
            scrollClear(nextScrollId)
            return ScrollResult.empty()
        }
        return ScrollResult.of(collect, nextScrollId)
    }

    override fun scrollClear(scrollId: String?) {
        if (!StringUtils.hasText(scrollId)) {
            return
        }
        client.clearScroll { it.scrollId(scrollId) }
    }

    override fun update(operator: UnaryOperator<UpdateRequest.Builder<T, T>>, documentId: String?): Boolean {
        val builder = operator.apply(
            UpdateRequest.Builder<T, T>()
                .id(documentId)
                // 刷新策略
                .refresh(Refresh.WaitFor)
                // 版本冲突时自动重试次数
                .retryOnConflict(5)
        )
        if (!info.hasMulti) {
            builder.index(info.index().first())
        }

        val response = client.update(builder.build(), cls)
        return response.result() == Result.Updated
    }

    override fun updateByQuery(builder: UpdateBuilder<T>, operator: UnaryOperator<UpdateByQueryRequest.Builder>): Long {
        merge(builder)
        val request = operator.apply(
            builder.build()
                .refresh(false)
        ).index(index()).build()
        val response = client.updateByQuery(request)
        return response.updated() ?: 0
    }

    override fun deleteByQuery(builder: SearchBuilder<T>, operator: UnaryOperator<DeleteByQueryRequest.Builder>): DeleteByQueryResponse {
        merge(builder)
        val request = operator.apply(
            DeleteByQueryRequest.Builder()
                .refresh(false)
                .query(builder.buildQuery())
        ).index(index()).build()

        return client.deleteByQuery(request)
    }

    override fun bulk(operations: List<BulkOperation>, operator: UnaryOperator<BulkRequest.Builder>): BulkResponse {
        val builder = operator.apply(BulkRequest.Builder())
        // 仅在单索引模式下指定默认的索引. 多索引需要构建的时候手动指定
        if (!info.hasMulti) {
            builder.index(index().first())
        }
        builder.operations(operations)
        return client.bulk(builder.build())
    }

}
