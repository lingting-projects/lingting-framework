package live.lingting.framework.elasticsearch.composer;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.util.ObjectBuilder;
import live.lingting.framework.elasticsearch.ElasticsearchFunction;
import lombok.experimental.UtilityClass;

import java.util.Collection;
import java.util.function.Function;

import static live.lingting.framework.elasticsearch.ElasticsearchUtils.fieldName;

/**
 * @author lingting 2024-03-06 17:40
 */
@UtilityClass
public class LambdaQueryComposer {

	public static <T> Query term(ElasticsearchFunction<?, T> func, T obj) {
		return term(func, obj, builder -> builder);
	}

	public static <T> Query term(ElasticsearchFunction<?, T> func, T obj,
			Function<TermQuery.Builder, ObjectBuilder<TermQuery>> operator) {
		String field = fieldName(func);
		return QueryComposer.term(field, obj, operator);
	}

	public static <T> Query terms(ElasticsearchFunction<?, T> func, Collection<T> objects) {
		String field = fieldName(func);
		return QueryComposer.terms(field, objects);
	}

	/**
	 * 小于
	 */
	public static <T> Query lt(ElasticsearchFunction<?, T> func, T obj) {
		String field = fieldName(func);
		return QueryComposer.lt(field, obj);
	}

	/**
	 * 小于等于
	 */
	public static <T> Query le(ElasticsearchFunction<?, T> func, T obj) {
		String field = fieldName(func);
		return QueryComposer.le(field, obj);
	}

	/**
	 * 大于
	 */
	public static <T> Query gt(ElasticsearchFunction<?, T> func, T obj) {
		String field = fieldName(func);
		return QueryComposer.gt(field, obj);
	}

	/**
	 * 大于等于
	 */
	public static <T> Query ge(ElasticsearchFunction<?, T> func, T obj) {
		String field = fieldName(func);
		return QueryComposer.ge(field, obj);
	}

	/**
	 * 大于等于 start 小于等于 end
	 */
	public static <T> Query between(ElasticsearchFunction<?, T> func, T start, T end) {
		String field = fieldName(func);
		return QueryComposer.between(field, start, end);
	}

	public static <T> Query wildcardAll(ElasticsearchFunction<?, T> func, T obj) {
		return wildcard(func, obj);
	}

	public static <T> Query wildcard(ElasticsearchFunction<?, T> func, T obj) {
		String field = fieldName(func);
		return QueryComposer.wildcard(field, obj);
	}

}
