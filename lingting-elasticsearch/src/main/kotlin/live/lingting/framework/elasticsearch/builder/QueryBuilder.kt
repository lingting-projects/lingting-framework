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
public class QueryBuilder<E> {

	private final List<Query> must = new ArrayList<>();

	private final List<Query> mustNot = new ArrayList<>();

	private final List<Query> should = new ArrayList<>();

	// region basic

	public QueryBuilder<E> merge(QueryBuilder<?> builder) {
		must.addAll(builder.must);
		mustNot.addAll(builder.mustNot);
		should.addAll(builder.should);
		return this;
	}

	public QueryBuilder<E> addMust(QueryBuilder<?> builder) {
		return addMust(builder.build());
	}

	public QueryBuilder<E> addMust(Query... queries) {
		return addMust(Arrays.asList(queries));
	}

	public QueryBuilder<E> addMust(Collection<Query> queries) {
		queries.stream().filter(Objects::nonNull).forEach(must::add);
		return this;
	}

	public QueryBuilder<E> addMust(boolean condition, Supplier<Query> supplier) {
		if (condition) {
			must.add(supplier.get());
		}
		return this;
	}

	public QueryBuilder<E> addMustNot(QueryBuilder<?> builder) {
		return addMustNot(builder.build());
	}

	public QueryBuilder<E> addMustNot(Query... queries) {
		return addMustNot(Arrays.asList(queries));
	}

	public QueryBuilder<E> addMustNot(Collection<Query> queries) {
		queries.stream().filter(Objects::nonNull).forEach(mustNot::add);
		return this;
	}

	public QueryBuilder<E> addShould(QueryBuilder<?> builder) {
		return addShould(builder.build());
	}

	public QueryBuilder<E> addShould(Query... queries) {
		return addShould(Arrays.asList(queries));
	}

	public QueryBuilder<E> addShould(Collection<Query> queries) {
		queries.stream().filter(Objects::nonNull).forEach(should::add);
		return this;
	}

	// endregion

	// region composer
	public <T> QueryBuilder<E> term(String field, T obj) {
		return addMust(QueryComposer.term(field, obj));
	}

	public <T> QueryBuilder<E> term(String field, T obj, TermOperator operator) {
		return addMust(QueryComposer.term(field, obj, operator));
	}

	public <T> QueryBuilder<E> terms(String field, Collection<T> objects) {
		return addMust(QueryComposer.terms(field, objects));
	}

	/**
	 * 小于
	 */
	public <T> QueryBuilder<E> lt(String field, T obj) {
		return addMust(QueryComposer.lt(field, obj));
	}

	/**
	 * 小于等于
	 */
	public <T> QueryBuilder<E> le(String field, T obj) {
		return addMust(QueryComposer.le(field, obj));
	}

	/**
	 * 大于
	 */
	public <T> QueryBuilder<E> gt(String field, T obj) {
		return addMust(QueryComposer.gt(field, obj));
	}

	/**
	 * 大于等于
	 */
	public <T> QueryBuilder<E> ge(String field, T obj) {
		return addMust(QueryComposer.ge(field, obj));
	}

	/**
	 * 大于等于 start 小于等于 end
	 */
	public <T> QueryBuilder<E> between(String field, T start, T end) {
		return addMust(QueryComposer.between(field, start, end));
	}

	public QueryBuilder<E> exists(String field) {
		return addMust(QueryComposer.exists(field));
	}

	public QueryBuilder<E> notExists(String field) {
		return addMust(QueryComposer.notExists(field));
	}

	public QueryBuilder<E> should(Query... queries) {
		return addMust(QueryComposer.should(queries));
	}

	public QueryBuilder<E> should(List<Query> queries) {
		return addMust(QueryComposer.should(queries));
	}

	public QueryBuilder<E> must(Query... queries) {
		return addMust(QueryComposer.must(queries));
	}

	public QueryBuilder<E> must(List<Query> queries) {
		return addMust(QueryComposer.must(queries));
	}

	public <T> QueryBuilder<E> wildcardAll(String field, T obj) {
		return addMust(QueryComposer.wildcardAll(field, obj));
	}

	public <T> QueryBuilder<E> wildcard(String field, T obj) {
		return addMust(QueryComposer.wildcard(field, obj));
	}

	public QueryBuilder<E> not(Query... queries) {
		return addMust(QueryComposer.not(queries));
	}

	public QueryBuilder<E> not(List<Query> queries) {
		return addMust(QueryComposer.not(queries));
	}

	public <T> QueryBuilder<E> term(ElasticsearchFunction<E, T> func, T obj) {
		return addMust(QueryComposer.term(func, obj));
	}

	public <T> QueryBuilder<E> term(ElasticsearchFunction<E, T> func, T obj, TermOperator operator) {
		return addMust(QueryComposer.term(func, obj, operator));
	}

	public <T> QueryBuilder<E> terms(ElasticsearchFunction<E, T> func, Collection<T> objects) {
		return addMust(QueryComposer.terms(func, objects));
	}

	/**
	 * 小于
	 */
	public <T> QueryBuilder<E> lt(ElasticsearchFunction<E, T> func, T obj) {
		return addMust(QueryComposer.lt(func, obj));
	}

	/**
	 * 小于等于
	 */
	public <T> QueryBuilder<E> le(ElasticsearchFunction<E, T> func, T obj) {
		return addMust(QueryComposer.le(func, obj));
	}

	/**
	 * 大于
	 */
	public <T> QueryBuilder<E> gt(ElasticsearchFunction<E, T> func, T obj) {
		return addMust(QueryComposer.gt(func, obj));
	}

	/**
	 * 大于等于
	 */
	public <T> QueryBuilder<E> ge(ElasticsearchFunction<E, T> func, T obj) {
		return addMust(QueryComposer.ge(func, obj));
	}

	/**
	 * 大于等于 start 小于等于 end
	 */
	public <T> QueryBuilder<E> between(ElasticsearchFunction<E, T> func, T start, T end) {
		return addMust(QueryComposer.between(func, start, end));
	}

	public <T> QueryBuilder<E> wildcardAll(ElasticsearchFunction<E, T> func, T obj) {
		return addMust(QueryComposer.wildcardAll(func, obj));
	}

	public <T> QueryBuilder<E> wildcard(ElasticsearchFunction<E, T> func, T obj) {
		return addMust(QueryComposer.wildcard(func, obj));
	}

	// endregion

	// region composer ifPresent
	public <T> QueryBuilder<E> termIfPresent(String field, T obj) {
		return addMust(isPresent(obj), () -> QueryComposer.term(field, obj));
	}

	public <T> QueryBuilder<E> termIfPresent(String field, T obj, TermOperator operator) {
		return addMust(isPresent(obj), () -> QueryComposer.term(field, obj, operator));
	}

	public <T> QueryBuilder<E> termsIfPresent(String field, Collection<T> objects) {
		return addMust(isPresent(objects), () -> QueryComposer.terms(field, objects));
	}

	/**
	 * 小于
	 */
	public <T> QueryBuilder<E> ltIfPresent(String field, T obj) {
		return addMust(isPresent(obj), () -> QueryComposer.lt(field, obj));
	}

	/**
	 * 小于等于
	 */
	public <T> QueryBuilder<E> leIfPresent(String field, T obj) {
		return addMust(isPresent(obj), () -> QueryComposer.le(field, obj));
	}

	/**
	 * 大于
	 */
	public <T> QueryBuilder<E> gtIfPresent(String field, T obj) {
		return addMust(isPresent(obj), () -> QueryComposer.gt(field, obj));
	}

	/**
	 * 大于等于
	 */
	public <T> QueryBuilder<E> geIfPresent(String field, T obj) {
		return addMust(isPresent(obj), () -> QueryComposer.ge(field, obj));
	}

	/**
	 * 大于等于 start 小于等于 end
	 */
	public <T> QueryBuilder<E> betweenIfPresent(String field, T start, T end) {
		return addMust(isPresent(start) && isPresent(end), () -> QueryComposer.between(field, start, end));
	}

	public <T> QueryBuilder<E> wildcardAllIfPresent(String field, T obj) {
		return addMust(isPresent(obj), () -> QueryComposer.wildcardAll(field, obj));
	}

	public <T> QueryBuilder<E> wildcardIfPresent(String field, T obj) {
		return addMust(isPresent(obj), () -> QueryComposer.wildcard(field, obj));
	}

	public <T> QueryBuilder<E> termIfPresent(ElasticsearchFunction<E, T> func, T obj) {
		return addMust(isPresent(obj), () -> QueryComposer.term(func, obj));
	}

	public <T> QueryBuilder<E> termIfPresent(ElasticsearchFunction<E, T> func, T obj, TermOperator operator) {
		return addMust(isPresent(obj), () -> QueryComposer.term(func, obj, operator));
	}

	public <T> QueryBuilder<E> termsIfPresent(ElasticsearchFunction<E, T> func, Collection<T> objects) {
		return addMust(isPresent(objects), () -> QueryComposer.terms(func, objects));
	}

	/**
	 * 小于
	 */
	public <T> QueryBuilder<E> ltIfPresent(ElasticsearchFunction<E, T> func, T obj) {
		return addMust(isPresent(obj), () -> QueryComposer.lt(func, obj));
	}

	/**
	 * 小于等于
	 */
	public <T> QueryBuilder<E> leIfPresent(ElasticsearchFunction<E, T> func, T obj) {
		return addMust(isPresent(obj), () -> QueryComposer.le(func, obj));
	}

	/**
	 * 大于
	 */
	public <T> QueryBuilder<E> gtIfPresent(ElasticsearchFunction<E, T> func, T obj) {
		return addMust(isPresent(obj), () -> QueryComposer.gt(func, obj));
	}

	/**
	 * 大于等于
	 */
	public <T> QueryBuilder<E> geIfPresent(ElasticsearchFunction<E, T> func, T obj) {
		return addMust(isPresent(obj), () -> QueryComposer.ge(func, obj));
	}

	/**
	 * 大于等于 start 小于等于 end
	 */
	public <T> QueryBuilder<E> betweenIfPresent(ElasticsearchFunction<E, T> func, T start, T end) {
		return addMust(isPresent(start) && isPresent(end), () -> QueryComposer.between(func, start, end));
	}

	public <T> QueryBuilder<E> wildcardAllIfPresent(ElasticsearchFunction<E, T> func, T obj) {
		return addMust(isPresent(obj), () -> QueryComposer.wildcardAll(func, obj));
	}

	public <T> QueryBuilder<E> wildcardIfPresent(ElasticsearchFunction<E, T> func, T obj) {
		return addMust(isPresent(obj), () -> QueryComposer.wildcard(func, obj));
	}

	// endregion

	public static <C> QueryBuilder<C> builder() {
		return new QueryBuilder<>();
	}

	public static <C> QueryBuilder<C> builder(Query... queries) {
		return new QueryBuilder<C>().addMust(queries);
	}

	public QueryBuilder<E> copy() {
		return new QueryBuilder<E>().merge(this);
	}

	public <T> QueryBuilder<T> to() {
		return new QueryBuilder<T>().merge(this);
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
