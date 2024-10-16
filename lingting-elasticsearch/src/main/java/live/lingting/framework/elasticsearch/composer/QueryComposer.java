package live.lingting.framework.elasticsearch.composer;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.util.ObjectBuilder;
import live.lingting.framework.elasticsearch.ElasticsearchFunction;
import live.lingting.framework.elasticsearch.function.TermOperator;
import live.lingting.framework.util.CollectionUtils;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static live.lingting.framework.elasticsearch.ElasticsearchUtils.fieldName;
import static live.lingting.framework.elasticsearch.ElasticsearchUtils.fieldValue;

/**
 * @author lingting 2024-03-06 17:33
 */
@UtilityClass
public class QueryComposer {

	// region basic
	public static <T> Query term(String field, T obj) {
		return term(field, obj, builder -> builder);
	}

	public static <T> Query term(String field, T obj, Function<TermQuery.Builder, ObjectBuilder<TermQuery>> operator) {
		FieldValue value = fieldValue(obj);
		return Query.of(qb -> qb.term(tq -> {
			TermQuery.Builder builder = tq.field(field).value(value);
			return operator.apply(builder);
		}));
	}

	public static <T> Query terms(String field, Collection<T> objects) {
		List<FieldValue> values = new ArrayList<>();

		if (!CollectionUtils.isEmpty(objects)) {
			for (Object object : objects) {
				if (object == null) {
					continue;
				}
				FieldValue value = fieldValue(object);
				values.add(value);
			}
		}

		return Query.of(qb -> qb.terms(tq -> tq.field(field).terms(tqf -> tqf.value(values))));
	}

	/**
	 * 小于
	 */
	public static <T> Query lt(String field, T obj) {
		JsonData value = JsonData.of(obj);
		return Query.of(qb -> qb.range(rb -> rb.untyped(ub -> ub.field(field).lt(value))));
	}

	/**
	 * 小于等于
	 */
	public static <T> Query le(String field, T obj) {
		JsonData value = JsonData.of(obj);
		return Query.of(qb -> qb.range(rq -> rq.untyped(ub -> ub.field(field).lte(value))));
	}

	/**
	 * 大于
	 */
	public static <T> Query gt(String field, T obj) {
		JsonData value = JsonData.of(obj);
		return Query.of(qb -> qb.range(rq -> rq.untyped(ub -> ub.field(field).gt(value))));
	}

	/**
	 * 大于等于
	 */
	public static <T> Query ge(String field, T obj) {
		JsonData value = JsonData.of(obj);
		return Query.of(qb -> qb.range(rq -> rq.untyped(ub -> ub.field(field).gte(value))));
	}

	/**
	 * 大于等于 start 小于等于 end
	 */
	public static <T> Query between(String field, T start, T end) {
		JsonData startData = JsonData.of(start);
		JsonData endData = JsonData.of(end);
		return Query.of(q -> q.range(rb -> rb.untyped(ub -> ub.field(field)
			// 大于等于 start
			.gte(startData)
			// 小于等于 end
			.lte(endData)

		)));
	}

	public static Query exists(String field) {
		return Query.of(q -> q.exists(e -> e.field(field)));
	}

	public static Query notExists(String field) {
		return Query.of(q -> q.bool(b -> b.mustNot(mn -> mn.exists(e -> e.field(field)))));
	}

	public static Query should(Query... queries) {
		return should(Arrays.asList(queries));
	}

	public static Query should(List<Query> queries) {
		return Query.of(q -> q.bool(b -> b.should(queries.stream().filter(Objects::nonNull).toList())));
	}

	public static Query must(Query... queries) {
		return must(Arrays.asList(queries));
	}

	public static Query must(List<Query> queries) {
		return Query.of(q -> q.bool(b -> b.must(queries.stream().filter(Objects::nonNull).toList())));
	}

	public static <T> Query wildcardAll(String field, T obj) {
		String format = String.format("*%s*", obj);
		return wildcard(field, format);
	}

	public static <T> Query wildcard(String field, T obj) {
		String value = obj.toString();
		return Query.of(qb -> qb.wildcard(wq -> wq.field(field).value(value)));
	}

	public static Query not(Query... queries) {
		return not(Arrays.asList(queries));
	}

	public static Query not(List<Query> queries) {
		return Query.of(q -> q.bool(b -> b.mustNot(queries.stream().filter(Objects::nonNull).toList())));
	}
	// endregion

	// region lambda
	public static <T> Query term(ElasticsearchFunction<?, T> func, T obj) {
		return term(func, obj, builder -> builder);
	}

	public static <T> Query term(ElasticsearchFunction<?, T> func, T obj, TermOperator operator) {
		String field = fieldName(func);
		return term(field, obj, operator);
	}

	public static <T> Query terms(ElasticsearchFunction<?, T> func, Collection<T> objects) {
		String field = fieldName(func);
		return terms(field, objects);
	}

	/**
	 * 小于
	 */
	public static <T> Query lt(ElasticsearchFunction<?, T> func, T obj) {
		String field = fieldName(func);
		return lt(field, obj);
	}

	/**
	 * 小于等于
	 */
	public static <T> Query le(ElasticsearchFunction<?, T> func, T obj) {
		String field = fieldName(func);
		return le(field, obj);
	}

	/**
	 * 大于
	 */
	public static <T> Query gt(ElasticsearchFunction<?, T> func, T obj) {
		String field = fieldName(func);
		return gt(field, obj);
	}

	/**
	 * 大于等于
	 */
	public static <T> Query ge(ElasticsearchFunction<?, T> func, T obj) {
		String field = fieldName(func);
		return ge(field, obj);
	}

	/**
	 * 大于等于 start 小于等于 end
	 */
	public static <T> Query between(ElasticsearchFunction<?, T> func, T start, T end) {
		String field = fieldName(func);
		return between(field, start, end);
	}

	public static <T> Query wildcardAll(ElasticsearchFunction<?, T> func, T obj) {
		String field = fieldName(func);
		return wildcardAll(field, obj);
	}

	public static <T> Query wildcard(ElasticsearchFunction<?, T> func, T obj) {
		String field = fieldName(func);
		return wildcard(field, obj);
	}

	// endregion

}
