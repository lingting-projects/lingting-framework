package live.lingting.framework.elasticsearch.composer;

import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import live.lingting.framework.elasticsearch.ElasticsearchFunction;

import java.util.function.UnaryOperator;

import static live.lingting.framework.elasticsearch.ElasticsearchUtils.fieldName;

/**
 * @author lingting 2024-03-06 17:47
 */
public final class AggComposer {

	private AggComposer() {throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");}

	public static Aggregation terms(String field) {
		return terms(field, null);
	}

	public static Aggregation terms(String field, Integer size) {
		return terms(field, size, builder -> builder);
	}

	public static <E> Aggregation terms(ElasticsearchFunction<E, ?> function) {
		return terms(fieldName(function));
	}

	public static <E> Aggregation terms(ElasticsearchFunction<E, ?> function, Integer size) {
		return terms(fieldName(function), size);
	}

	public static Aggregation terms(String field, Integer size,
									UnaryOperator<Aggregation.Builder.ContainerBuilder> operator) {
		return Aggregation.of(agg -> {
			Aggregation.Builder.ContainerBuilder builder = agg.terms(ta -> {
				if (size != null) {
					ta.size(size);
				}
				return ta.field(field);
			});
			return operator.apply(builder);
		});
	}

	public static <E> Aggregation terms(ElasticsearchFunction<E, ?> function,
										UnaryOperator<Aggregation.Builder.ContainerBuilder> operator) {
		return terms(function, null, operator);
	}

	public static <E> Aggregation terms(ElasticsearchFunction<E, ?> function, Integer size,
										UnaryOperator<Aggregation.Builder.ContainerBuilder> operator) {
		return terms(fieldName(function), size, operator);
	}

}
