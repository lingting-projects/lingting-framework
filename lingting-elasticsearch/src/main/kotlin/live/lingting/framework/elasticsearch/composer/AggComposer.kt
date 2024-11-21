package live.lingting.framework.elasticsearch.composer

import co.elastic.clients.elasticsearch._types.aggregations.Aggregation
import co.elastic.clients.elasticsearch._types.aggregations.TermsAggregation
import java.util.function.UnaryOperator
import live.lingting.framework.elasticsearch.ElasticsearchFunction
import live.lingting.framework.elasticsearch.ElasticsearchUtils

/**
 * @author lingting 2024-03-06 17:47
 */
object AggComposer {
    @JvmStatic
    fun <E> terms(function: ElasticsearchFunction<E, *>): Aggregation {
        return terms(ElasticsearchUtils.fieldName(function))
    }

    @JvmStatic
    fun <E> terms(function: ElasticsearchFunction<E, *>, size: Int): Aggregation {
        return terms(ElasticsearchUtils.fieldName(function), size)
    }

    @JvmStatic
    @JvmOverloads
    fun terms(
        field: String, size: Int? = null,
        operator: UnaryOperator<Aggregation.Builder.ContainerBuilder> = UnaryOperator { builder -> builder }
    ): Aggregation {
        return Aggregation.of { agg ->
            val builder = agg.terms { ta ->
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
        operator: UnaryOperator<Aggregation.Builder.ContainerBuilder>
    ): Aggregation {
        return terms(function, null, operator)
    }

    @JvmStatic
    fun <E> terms(
        function: ElasticsearchFunction<E, *>, size: Int?,
        operator: UnaryOperator<Aggregation.Builder.ContainerBuilder>
    ): Aggregation {
        val field = ElasticsearchUtils.fieldName(function)
        return terms(field, size, operator)
    }
}
