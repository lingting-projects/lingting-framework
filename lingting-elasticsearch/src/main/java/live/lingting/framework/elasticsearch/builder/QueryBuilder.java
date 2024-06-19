package live.lingting.framework.elasticsearch.builder;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import live.lingting.framework.elasticsearch.ElasticsearchFunction;
import live.lingting.framework.elasticsearch.composer.QueryComposer;
import live.lingting.framework.elasticsearch.function.TermOperator;
import live.lingting.framework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import static live.lingting.framework.util.ValueUtils.isPresent;

/**
 * @author lingting 2024-06-17 17:06
 */
public class QueryBuilder<C> {

	private final List<Query> must = new ArrayList<>();

	private final List<Query> mustNot = new ArrayList<>();

	private final List<Query> should = new ArrayList<>();

	// region basic

	public QueryBuilder<C> merge(QueryBuilder<?> builder) {
		must.addAll(builder.must);
		mustNot.addAll(builder.mustNot);
		should.addAll(builder.should);
		return this;
	}

	public QueryBuilder<C> addMust(QueryBuilder<?> builder) {
		return addMust(builder.build());
	}

	public QueryBuilder<C> addMust(Query... queries) {
		return addMust(Arrays.asList(queries));
	}

	public QueryBuilder<C> addMust(Collection<Query> queries) {
		queries.stream().filter(Objects::nonNull).forEach(must::add);
		return this;
	}

	public QueryBuilder<C> addMust(boolean condition, Supplier<Query> supplier) {
		if (condition) {
			must.add(supplier.get());
		}
		return this;
	}

	public QueryBuilder<C> addMustNot(QueryBuilder<?> builder) {
		return addMustNot(builder.build());
	}

	public QueryBuilder<C> addMustNot(Query... queries) {
		return addMustNot(Arrays.asList(queries));
	}

	public QueryBuilder<C> addMustNot(Collection<Query> queries) {
		queries.stream().filter(Objects::nonNull).forEach(mustNot::add);
		return this;
	}

	public QueryBuilder<C> addShould(QueryBuilder<?> builder) {
		return addShould(builder.build());
	}

	public QueryBuilder<C> addShould(Query... queries) {
		return addShould(Arrays.asList(queries));
	}

	public QueryBuilder<C> addShould(Collection<Query> queries) {
		queries.stream().filter(Objects::nonNull).forEach(should::add);
		return this;
	}

	// endregion

	// region composer
	public <T> QueryBuilder<C> term(String field, T obj) {
		return addMust(QueryComposer.term(field, obj));
	}

	public <T> QueryBuilder<C> term(String field, T obj, TermOperator operator) {
		return addMust(QueryComposer.term(field, obj, operator));
	}

	public <T> QueryBuilder<C> terms(String field, Collection<T> objects) {
		return addMust(QueryComposer.terms(field, objects));
	}

	/**
	 * 小于
	 */
	public <T> QueryBuilder<C> lt(String field, T obj) {
		return addMust(QueryComposer.lt(field, obj));
	}

	/**
	 * 小于等于
	 */
	public <T> QueryBuilder<C> le(String field, T obj) {
		return addMust(QueryComposer.le(field, obj));
	}

	/**
	 * 大于
	 */
	public <T> QueryBuilder<C> gt(String field, T obj) {
		return addMust(QueryComposer.gt(field, obj));
	}

	/**
	 * 大于等于
	 */
	public <T> QueryBuilder<C> ge(String field, T obj) {
		return addMust(QueryComposer.ge(field, obj));
	}

	/**
	 * 大于等于 start 小于等于 end
	 */
	public <T> QueryBuilder<C> between(String field, T start, T end) {
		return addMust(QueryComposer.between(field, start, end));
	}

	public QueryBuilder<C> exists(String field) {
		return addMust(QueryComposer.exists(field));
	}

	public QueryBuilder<C> notExists(String field) {
		return addMust(QueryComposer.notExists(field));
	}

	public QueryBuilder<C> should(Query... queries) {
		return addMust(QueryComposer.should(queries));
	}

	public QueryBuilder<C> should(List<Query> queries) {
		return addMust(QueryComposer.should(queries));
	}

	public QueryBuilder<C> must(Query... queries) {
		return addMust(QueryComposer.must(queries));
	}

	public QueryBuilder<C> must(List<Query> queries) {
		return addMust(QueryComposer.must(queries));
	}

	public <T> QueryBuilder<C> wildcardAll(String field, T obj) {
		return addMust(QueryComposer.wildcardAll(field, obj));
	}

	public <T> QueryBuilder<C> wildcard(String field, T obj) {
		return addMust(QueryComposer.wildcard(field, obj));
	}

	public QueryBuilder<C> not(Query... queries) {
		return addMust(QueryComposer.not(queries));
	}

	public QueryBuilder<C> not(List<Query> queries) {
		return addMust(QueryComposer.not(queries));
	}

	public <T> QueryBuilder<C> term(ElasticsearchFunction<?, T> func, T obj) {
		return addMust(QueryComposer.term(func, obj));
	}

	public <T> QueryBuilder<C> term(ElasticsearchFunction<?, T> func, T obj, TermOperator operator) {
		return addMust(QueryComposer.term(func, obj, operator));
	}

	public <T> QueryBuilder<C> terms(ElasticsearchFunction<?, T> func, Collection<T> objects) {
		return addMust(QueryComposer.terms(func, objects));
	}

	/**
	 * 小于
	 */
	public <T> QueryBuilder<C> lt(ElasticsearchFunction<?, T> func, T obj) {
		return addMust(QueryComposer.lt(func, obj));
	}

	/**
	 * 小于等于
	 */
	public <T> QueryBuilder<C> le(ElasticsearchFunction<?, T> func, T obj) {
		return addMust(QueryComposer.le(func, obj));
	}

	/**
	 * 大于
	 */
	public <T> QueryBuilder<C> gt(ElasticsearchFunction<?, T> func, T obj) {
		return addMust(QueryComposer.gt(func, obj));
	}

	/**
	 * 大于等于
	 */
	public <T> QueryBuilder<C> ge(ElasticsearchFunction<?, T> func, T obj) {
		return addMust(QueryComposer.ge(func, obj));
	}

	/**
	 * 大于等于 start 小于等于 end
	 */
	public <T> QueryBuilder<C> between(ElasticsearchFunction<?, T> func, T start, T end) {
		return addMust(QueryComposer.between(func, start, end));
	}

	public <T> QueryBuilder<C> wildcardAll(ElasticsearchFunction<?, T> func, T obj) {
		return addMust(QueryComposer.wildcardAll(func, obj));
	}

	public <T> QueryBuilder<C> wildcard(ElasticsearchFunction<?, T> func, T obj) {
		return addMust(QueryComposer.wildcard(func, obj));
	}

	// endregion

	// region composer ifPresent
	public <T> QueryBuilder<C> termIfPresent(String field, T obj) {
		return addMust(isPresent(obj), () -> QueryComposer.term(field, obj));
	}

	public <T> QueryBuilder<C> termIfPresent(String field, T obj, TermOperator operator) {
		return addMust(isPresent(obj), () -> QueryComposer.term(field, obj, operator));
	}

	public <T> QueryBuilder<C> termsIfPresent(String field, Collection<T> objects) {
		return addMust(isPresent(objects), () -> QueryComposer.terms(field, objects));
	}

	/**
	 * 小于
	 */
	public <T> QueryBuilder<C> ltIfPresent(String field, T obj) {
		return addMust(isPresent(obj), () -> QueryComposer.lt(field, obj));
	}

	/**
	 * 小于等于
	 */
	public <T> QueryBuilder<C> leIfPresent(String field, T obj) {
		return addMust(isPresent(obj), () -> QueryComposer.le(field, obj));
	}

	/**
	 * 大于
	 */
	public <T> QueryBuilder<C> gtIfPresent(String field, T obj) {
		return addMust(isPresent(obj), () -> QueryComposer.gt(field, obj));
	}

	/**
	 * 大于等于
	 */
	public <T> QueryBuilder<C> geIfPresent(String field, T obj) {
		return addMust(isPresent(obj), () -> QueryComposer.ge(field, obj));
	}

	/**
	 * 大于等于 start 小于等于 end
	 */
	public <T> QueryBuilder<C> betweenIfPresent(String field, T start, T end) {
		return addMust(isPresent(start) && isPresent(end), () -> QueryComposer.between(field, start, end));
	}

	public <T> QueryBuilder<C> wildcardAllIfPresent(String field, T obj) {
		return addMust(isPresent(obj), () -> QueryComposer.wildcardAll(field, obj));
	}

	public <T> QueryBuilder<C> wildcardIfPresent(String field, T obj) {
		return addMust(isPresent(obj), () -> QueryComposer.wildcard(field, obj));
	}

	public <T> QueryBuilder<C> termIfPresent(ElasticsearchFunction<?, T> func, T obj) {
		return addMust(isPresent(obj), () -> QueryComposer.term(func, obj));
	}

	public <T> QueryBuilder<C> termIfPresent(ElasticsearchFunction<?, T> func, T obj, TermOperator operator) {
		return addMust(isPresent(obj), () -> QueryComposer.term(func, obj, operator));
	}

	public <T> QueryBuilder<C> termsIfPresent(ElasticsearchFunction<?, T> func, Collection<T> objects) {
		return addMust(isPresent(objects), () -> QueryComposer.terms(func, objects));
	}

	/**
	 * 小于
	 */
	public <T> QueryBuilder<C> ltIfPresent(ElasticsearchFunction<?, T> func, T obj) {
		return addMust(isPresent(obj), () -> QueryComposer.lt(func, obj));
	}

	/**
	 * 小于等于
	 */
	public <T> QueryBuilder<C> leIfPresent(ElasticsearchFunction<?, T> func, T obj) {
		return addMust(isPresent(obj), () -> QueryComposer.le(func, obj));
	}

	/**
	 * 大于
	 */
	public <T> QueryBuilder<C> gtIfPresent(ElasticsearchFunction<?, T> func, T obj) {
		return addMust(isPresent(obj), () -> QueryComposer.gt(func, obj));
	}

	/**
	 * 大于等于
	 */
	public <T> QueryBuilder<C> geIfPresent(ElasticsearchFunction<?, T> func, T obj) {
		return addMust(isPresent(obj), () -> QueryComposer.ge(func, obj));
	}

	/**
	 * 大于等于 start 小于等于 end
	 */
	public <T> QueryBuilder<C> betweenIfPresent(ElasticsearchFunction<?, T> func, T start, T end) {
		return addMust(isPresent(start) && isPresent(end), () -> QueryComposer.between(func, start, end));
	}

	public <T> QueryBuilder<C> wildcardAllIfPresent(ElasticsearchFunction<?, T> func, T obj) {
		return addMust(isPresent(obj), () -> QueryComposer.wildcardAll(func, obj));
	}

	public <T> QueryBuilder<C> wildcardIfPresent(ElasticsearchFunction<?, T> func, T obj) {
		return addMust(isPresent(obj), () -> QueryComposer.wildcard(func, obj));
	}

	// endregion

	public static <C> QueryBuilder<C> builder() {
		return new QueryBuilder<>();
	}

	public Query build() {
		BoolQuery.Builder builder = new BoolQuery.Builder();
		if (!CollectionUtils.isEmpty(must)) {
			builder.must(new ArrayList<>(must));
		}
		if (!CollectionUtils.isEmpty(should)) {
			builder.should(new ArrayList<>(should));
		}
		if (!CollectionUtils.isEmpty(mustNot)) {
			builder.mustNot(new ArrayList<>(mustNot));
		}

		Query.Builder queryBuilder = new Query.Builder();
		queryBuilder.bool(builder.build());
		return queryBuilder.build();
	}

}
