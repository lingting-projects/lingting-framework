package live.lingting.framework.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ErrorCause;
import co.elastic.clients.elasticsearch._types.Refresh;
import co.elastic.clients.elasticsearch._types.Result;
import co.elastic.clients.elasticsearch._types.Script;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.Time;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.DeleteByQueryRequest;
import co.elastic.clients.elasticsearch.core.DeleteByQueryResponse;
import co.elastic.clients.elasticsearch.core.GetRequest;
import co.elastic.clients.elasticsearch.core.ScrollRequest;
import co.elastic.clients.elasticsearch.core.ScrollResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.UpdateByQueryRequest;
import co.elastic.clients.elasticsearch.core.UpdateByQueryResponse;
import co.elastic.clients.elasticsearch.core.UpdateRequest;
import co.elastic.clients.elasticsearch.core.UpdateResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import co.elastic.clients.elasticsearch.core.search.TrackHits;
import co.elastic.clients.util.ObjectBuilder;
import live.lingting.framework.api.LimitCursor;
import live.lingting.framework.api.PaginationParams;
import live.lingting.framework.api.PaginationResult;
import live.lingting.framework.api.ScrollCursor;
import live.lingting.framework.api.ScrollParams;
import live.lingting.framework.api.ScrollResult;
import live.lingting.framework.elasticsearch.composer.SortComposer;
import live.lingting.framework.elasticsearch.datascope.ElasticsearchDataPermissionHandler;
import live.lingting.framework.function.ThrowingRunnable;
import live.lingting.framework.function.ThrowingSupplier;
import live.lingting.framework.retry.Retry;
import live.lingting.framework.util.CollectionUtils;
import live.lingting.framework.util.StringUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * @author lingting 2024-03-06 16:41
 */
@Slf4j
@Getter
public class ElasticsearchApi<T> {

	private final String index;

	private final Class<T> cls;

	private final Function<T, String> idFunc;

	private final ElasticsearchDataPermissionHandler handler;

	private final ElasticsearchClient client;

	private final ElasticsearchProperties.Retry retryProperties;

	private final Long scrollSize;

	private final Time scrollTime;

	public ElasticsearchApi(Class<T> cls, Function<T, String> idFunc, ElasticsearchProperties properties,
			ElasticsearchDataPermissionHandler handler, ElasticsearchClient client) {
		this(ElasticsearchUtils.index(cls), cls, idFunc, properties, handler, client);
	}

	public ElasticsearchApi(String index, Class<T> cls, Function<T, String> idFunc, ElasticsearchProperties properties,
			ElasticsearchDataPermissionHandler handler, ElasticsearchClient client) {
		this.index = index;
		this.cls = cls;
		this.idFunc = idFunc;
		this.handler = handler;
		this.client = client;
		this.retryProperties = properties.getRetry();
		Long currentScrollSize = null;
		Time currentScrollTime = null;

		ElasticsearchProperties.Scroll scroll = properties.getScroll();
		if (scroll != null) {
			currentScrollSize = scroll.getSize();
			if (scroll.getTimeout() != null) {
				currentScrollTime = Time.of(t -> t.time("%ds".formatted(scroll.getTimeout().toSeconds())));
			}
		}

		this.scrollSize = currentScrollSize;
		this.scrollTime = currentScrollTime;
	}

	public String documentId(T t) {
		return idFunc.apply(t);
	}

	public void retry(ThrowingRunnable runnable) throws Exception {
		retry(() -> {
			runnable.run();
			return null;
		});
	}

	public <R> R retry(ThrowingSupplier<R> supplier) throws Exception {
		if (retryProperties == null || !retryProperties.isEnabled()) {
			return supplier.get();
		}

		Retry<R> retry = new ElasticsearchRetry<>(retryProperties, supplier);
		return retry.get();
	}

	public Query merge(Query... arrays) {
		List<Query> queries = new ArrayList<>();
		Arrays.stream(arrays).filter(Objects::nonNull).forEach(queries::add);

		if (handler != null && !handler.ignorePermissionControl(index)) {
			handler.filterDataScopes(index).forEach(scope -> queries.add(scope.invoke(index)));
		}

		Query.Builder qb = new Query.Builder();
		qb.bool(bq -> bq.must(queries));
		return qb.build();
	}

	public T get(String id) throws IOException {
		GetRequest request = GetRequest.of(gr -> gr.index(index).id(id));
		return client.get(request, cls).source();
	}

	public T getByQuery(Query... queries) throws IOException {
		return getByQuery(builder -> builder, queries);
	}

	public T getByQuery(UnaryOperator<SearchRequest.Builder> operator, Query... queries) throws IOException {
		return search(builder -> operator.apply(builder).size(1), queries).hits()
			.stream()
			.findFirst()
			.map(Hit::source)
			.orElse(null);
	}

	public long count(Query... queries) throws IOException {
		HitsMetadata<T> metadata = search(builder -> builder.size(0), queries);
		TotalHits hits = metadata.total();
		return hits == null ? 0 : hits.value();
	}

	public HitsMetadata<T> search(Query... queries) throws IOException {
		return search(builder -> builder, queries);
	}

	public HitsMetadata<T> search(UnaryOperator<SearchRequest.Builder> operator, Query... queries) throws IOException {
		Query query = merge(queries);

		SearchRequest.Builder builder = operator.apply(new SearchRequest.Builder()
			// 返回匹配的所有文档数量
			.trackTotalHits(TrackHits.of(th -> th.enabled(true)))

		);
		builder.index(index);
		builder.query(query);

		SearchResponse<T> searchResponse = client.search(builder.build(), cls);
		return searchResponse.hits();
	}

	public List<SortOptions> ofLimitSort(Collection<PaginationParams.Sort> sorts) {
		if (CollectionUtils.isEmpty(sorts)) {
			return new ArrayList<>();
		}
		return sorts.stream().map(sort -> {
			String field = StringUtils.underscoreToHump(sort.getField());
			return SortComposer.sort(field, sort.getDesc());
		}).toList();
	}

	public PaginationResult<T> page(PaginationParams params, Query... queries) throws IOException {
		List<SortOptions> sorts = ofLimitSort(params.getSorts());

		int from = (int) params.start();
		int size = (int) params.getSize();

		HitsMetadata<T> hitsMetadata = search(builder -> builder.size(size).from(from).sort(sorts), queries);

		List<T> list = hitsMetadata.hits().stream().map(Hit::source).toList();
		long total = Optional.ofNullable(hitsMetadata.total()).map(TotalHits::value).orElse(0L);

		return new PaginationResult<>(total, list);
	}

	public void aggs(BiConsumer<String, Aggregate> consumer, Map<String, Aggregation> aggregationMap, Query... queries)
			throws IOException {
		aggs(builder -> builder, consumer, aggregationMap, queries);
	}

	public void aggs(UnaryOperator<SearchRequest.Builder> operator, BiConsumer<String, Aggregate> consumer,
			Map<String, Aggregation> aggregationMap, Query... queries) throws IOException {
		aggs(operator, response -> {
			Map<String, Aggregate> aggregations = response.aggregations();
			Set<Map.Entry<String, Aggregate>> entries = aggregations.entrySet();
			for (Map.Entry<String, Aggregate> entry : entries) {
				String key = entry.getKey();
				Aggregate aggregate = entry.getValue();
				consumer.accept(key, aggregate);
			}
		}, aggregationMap, queries);
	}

	public void aggs(UnaryOperator<SearchRequest.Builder> operator, Consumer<SearchResponse<T>> consumer,
			Map<String, Aggregation> aggregationMap, Query... queries) throws IOException {

		Query query = merge(queries);

		SearchRequest.Builder builder = operator.apply(new SearchRequest.Builder()
			// 返回匹配的所有文档数量
			.trackTotalHits(TrackHits.of(th -> th.enabled(true)))

		);
		builder.size(0);
		builder.index(index);
		builder.query(query);
		builder.aggregations(aggregationMap);

		SearchResponse<T> response = client.search(builder.build(), cls);
		consumer.accept(response);
	}

	public boolean update(String documentId, Function<Script.Builder, ObjectBuilder<Script>> scriptOperator)
			throws IOException {
		return update(documentId, scriptOperator.apply(new Script.Builder()).build());
	}

	public boolean update(String documentId, Script script) throws IOException {
		return update(builder -> builder, documentId, script);
	}

	public boolean update(UnaryOperator<UpdateRequest.Builder<T, T>> operator, String documentId, Script script)
			throws IOException {
		return update(builder -> operator.apply(builder).script(script), documentId);
	}

	public boolean update(T t) throws IOException {
		return update(builder -> builder.doc(t), documentId(t));
	}

	public boolean upsert(T doc) throws IOException {
		return update(builder -> builder.doc(doc).docAsUpsert(true), documentId(doc));
	}

	public boolean upsert(T doc, Script script) throws IOException {
		return update(builder -> builder.doc(doc).script(script), documentId(doc));
	}

	public boolean update(UnaryOperator<UpdateRequest.Builder<T, T>> operator, String documentId) throws IOException {
		UpdateRequest.Builder<T, T> builder = operator.apply(new UpdateRequest.Builder<T, T>()
			// 刷新策略
			.refresh(Refresh.WaitFor)
			// 版本冲突时自动重试次数
			.retryOnConflict(5));

		builder.index(index).id(documentId);

		UpdateResponse<T> response = client.update(builder.build(), cls);
		Result result = response.result();
		return Result.Updated.equals(result);
	}

	public boolean updateByQuery(Function<Script.Builder, ObjectBuilder<Script>> scriptOperator, Query... queries)
			throws IOException {
		return updateByQuery(scriptOperator.apply(new Script.Builder()).build(), queries);
	}

	public boolean updateByQuery(Script script, Query... queries) throws IOException {
		return updateByQuery(builder -> builder, script, queries);
	}

	public boolean updateByQuery(UnaryOperator<UpdateByQueryRequest.Builder> operator, Script script, Query... queries)
			throws IOException {
		Query query = merge(queries);

		UpdateByQueryRequest.Builder builder = operator.apply(new UpdateByQueryRequest.Builder()
			// 刷新策略
			.refresh(false));
		builder.index(index).query(query).script(script);

		UpdateByQueryResponse response = client.updateByQuery(builder.build());
		Long total = response.total();
		return total != null && total > 0;
	}

	public BulkResponse bulk(BulkOperation... operations) throws IOException {
		return bulk(Arrays.stream(operations).toList());
	}

	public BulkResponse bulk(List<BulkOperation> operations) throws IOException {
		return bulk(builder -> builder, operations);
	}

	public BulkResponse bulk(UnaryOperator<BulkRequest.Builder> operator, List<BulkOperation> operations)
			throws IOException {
		BulkRequest.Builder builder = operator.apply(new BulkRequest.Builder().refresh(Refresh.WaitFor));
		builder.index(index);
		builder.operations(operations);
		return client.bulk(builder.build());
	}

	public void save(T t) throws IOException {
		saveBatch(Collections.singleton(t));
	}

	public void saveBatch(Collection<T> collection) throws IOException {
		saveBatch(builder -> builder, collection);
	}

	public void saveBatch(UnaryOperator<BulkRequest.Builder> operator, Collection<T> collection) throws IOException {
		batch(operator, collection, t -> {
			String documentId = documentId(t);

			BulkOperation.Builder ob = new BulkOperation.Builder();
			ob.create(create -> create.id(documentId).document(t));
			return ob.build();
		});
	}

	public <E> BulkResponse batch(Collection<E> collection, Function<E, BulkOperation> function) throws IOException {
		return batch(builder -> builder, collection, function);
	}

	public <E> BulkResponse batch(UnaryOperator<BulkRequest.Builder> operator, Collection<E> collection,
			Function<E, BulkOperation> function) throws IOException {
		if (CollectionUtils.isEmpty(collection)) {
			return BulkResponse.of(br -> br.errors(false).items(Collections.emptyList()).ingestTook(0L).took(0));
		}

		List<BulkOperation> operations = new ArrayList<>();

		for (E e : collection) {
			operations.add(function.apply(e));
		}

		BulkResponse response = bulk(builder -> operator.apply(builder.refresh(Refresh.WaitFor)), operations);
		if (response.errors()) {
			List<BulkResponseItem> collect = response.items().stream().filter(item -> item.error() != null).toList();
			boolean allError = collect.size() == collection.size();
			for (int i = (allError ? 1 : 0); i < collect.size(); i++) {
				ErrorCause error = collect.get(i).error();
				log.warn("save error: {}", error);
			}

			// 全部保存失败, 抛异常
			if (allError) {
				throw new IOException("bulk save error! " + collect.get(0).error());
			}
		}
		return response;
	}

	public boolean deleteByQuery(Query... queries) throws IOException {
		return deleteByQuery(builder -> builder, queries);
	}

	public boolean deleteByQuery(UnaryOperator<DeleteByQueryRequest.Builder> operator, Query... queries)
			throws IOException {
		Query query = merge(queries);

		DeleteByQueryRequest.Builder builder = operator.apply(new DeleteByQueryRequest.Builder().refresh(false));
		builder.index(index);
		builder.query(query);

		DeleteByQueryResponse response = client.deleteByQuery(builder.build());
		Long deleted = response.deleted();
		return deleted != null && deleted > 0;
	}

	public List<T> list(Query... queries) throws IOException {
		return list(builder -> builder, queries);
	}

	public List<T> list(UnaryOperator<SearchRequest.Builder> operator, Query... queries) throws IOException {
		List<T> list = new ArrayList<>();

		ScrollParams<String> params = new ScrollParams<>(scrollSize, null);
		List<T> records;

		do {
			ScrollResult<T, String> result = scroll(operator, params, queries);
			records = result.getRecords();
			params.setCursor(result.getCursor());

			if (!CollectionUtils.isEmpty(records)) {
				list.addAll(records);
			}

		}
		while (!CollectionUtils.isEmpty(records) && params.getCursor() != null);

		return list;
	}

	public ScrollResult<T, String> scroll(ScrollParams<String> params, Query... queries) throws IOException {
		return scroll(builder -> builder, params, queries);
	}

	public ScrollResult<T, String> scroll(UnaryOperator<SearchRequest.Builder> operator, ScrollParams<String> params,
			Query... queries) throws IOException {
		String scrollId = null;
		if (params.getCursor() != null) {
			scrollId = params.getCursor();
		}
		// 非首次滚动查询, 直接使用 scrollId
		if (StringUtils.hasText(scrollId)) {
			return scroll(builder -> builder.scroll(scrollTime), scrollId);
		}

		Query query = merge(queries);
		SearchRequest.Builder builder = operator.apply(new SearchRequest.Builder().scroll(scrollTime)
			// 返回匹配的所有文档数量
			.trackTotalHits(TrackHits.of(th -> th.enabled(true)))).index(index).query(query);

		if (params.getSize() != null) {
			builder.size(params.getSize().intValue());
		}

		SearchResponse<T> search = client.search(builder.build(), cls);
		List<T> collect = search.hits().hits().stream().map(Hit::source).filter(Objects::nonNull).toList();

		String nextScrollId = search.scrollId();

		// 如果首次滚动查询结果为空, 直接清除滚动上下文
		if (CollectionUtils.isEmpty(collect)) {
			clearScroll(nextScrollId);
		}

		return ScrollResult.of(collect, nextScrollId);
	}

	public ScrollResult<T, String> scroll(UnaryOperator<ScrollRequest.Builder> operator, String scrollId)
			throws IOException {
		ScrollRequest.Builder builder = operator.apply(new ScrollRequest.Builder()).scrollId(scrollId);

		ScrollResponse<T> response = client.scroll(builder.build(), cls);
		List<T> collect = response.hits().hits().stream().map(Hit::source).toList();
		String nextScrollId = response.scrollId();

		if (CollectionUtils.isEmpty(collect)) {
			clearScroll(nextScrollId);
			return ScrollResult.empty();
		}
		return ScrollResult.of(collect, nextScrollId);
	}

	public void clearScroll(String scrollId) throws IOException {
		if (!StringUtils.hasText(scrollId)) {
			return;
		}
		client.clearScroll(scr -> scr.scrollId(scrollId));
	}

	public LimitCursor<T> pageCursor(PaginationParams params, Query... queries) {
		return new LimitCursor<>(page -> {
			params.setPage(page);
			return page(params, queries);
		});
	}

	public ScrollCursor<T, String> scrollCursor(ScrollParams<String> params, Query... queries) throws IOException {
		ScrollResult<T, String> scroll = scroll(params, queries);
		return new ScrollCursor<>(scrollId -> {
			params.setCursor(scrollId);
			return scroll(params, queries);
		}, scroll.getCursor(), scroll.getRecords());
	}

}
