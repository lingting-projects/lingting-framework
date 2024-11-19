package live.lingting.framework.elasticsearch.composer

import co.elastic.clients.elasticsearch._types.aggregations.Aggregation
import co.elastic.clients.elasticsearch._types.aggregations.TermsAggregation
import java.util.function.UnaryOperator
import live.lingting.framework.elasticsearch.ElasticsearchFunction
import live.lingting.framework.elasticsearch.ElasticsearchUtils

/**
 * @author lingting 2024-03-06 17:47
 */
class AggComposer private constructor() {
    init {
        throw UnsupportedOperationException("This is a utility class and cannot be instantiated")
    }

    companion object {
        fun <E> terms(function: ElasticsearchFunction<E, *>): Aggregation {
            return terms(ElasticsearchUtils.fieldName(function))
        }

        fun <E> terms(function: ElasticsearchFunction<E, *>, size: Int?): Aggregation {
            return terms(ElasticsearchUtils.fieldName(function), size)
        }

        @JvmOverloads
        fun terms(
            field: String?, size: Int? = null,
            operator: UnaryOperator<Aggregation.Builder.ContainerBuilder?> = UnaryOperator { builder: Aggregation.Builder.ContainerBuilder? -> builder }
        ): Aggregation {
            return Aggregation.of { agg: Aggregation.Builder ->
                val builder = agg.terms { ta: TermsAggregation.Builder ->
                    if (size != null) {
                        ta.size(size)
                    }
                    ta.field(field)
                }
                operator.apply(builder)
            }
        }

        fun <E> terms(
            function: ElasticsearchFunction<E, *>,
            operator: UnaryOperator<Aggregation.Builder.ContainerBuilder?>
        ): Aggregation {
            return terms(function, null, operator)
        }

        fun <E> terms(
            function: ElasticsearchFunction<E, *>, size: Int?,
            operator: UnaryOperator<Aggregation.Builder.ContainerBuilder?>
        ): Aggregation {
            return terms(ElasticsearchUtils.fieldName(function), size, operator)
        }
    }
}
