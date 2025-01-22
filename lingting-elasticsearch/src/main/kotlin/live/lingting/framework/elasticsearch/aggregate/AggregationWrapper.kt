package live.lingting.framework.elasticsearch.aggregate

import co.elastic.clients.elasticsearch._types.aggregations.Aggregate
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation
import java.util.function.Consumer
import java.util.function.UnaryOperator
import live.lingting.framework.elasticsearch.EFunction
import live.lingting.framework.elasticsearch.util.ElasticsearchUtils

/**
 * @author lingting 2025/1/22 11:25
 */
data class AggregationWrapper(
    val key: String,
    val value: Aggregation,
    val on: Consumer<Aggregate>?
) {

    companion object {

        @JvmStatic
        @JvmOverloads
        fun terms(
            key: String, field: String, size: Int? = null,
            operator: UnaryOperator<Aggregation.Builder.ContainerBuilder>? = null
        ): Aggregation {
            return Aggregation.of { agg ->
                val builder = agg.terms { ta ->
                    if (size != null) {
                        ta.size(size)
                    }
                    ta.field(field)
                }
                operator?.apply(builder)
            }
        }

        @JvmStatic
        @JvmOverloads
        fun <E> terms(
            key: String, function: EFunction<E, *>, size: Int? = null,
            operator: UnaryOperator<Aggregation.Builder.ContainerBuilder>? = null
        ): Aggregation {
            val field = ElasticsearchUtils.fieldName(function)
            return terms(key, field, size, operator)
        }

    }

    fun on(on: Consumer<Aggregate>?): AggregationWrapper {
        return copy(on = on)
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AggregationWrapper) return false
        return key == other.key
    }

    override fun hashCode(): Int {
        return key.hashCode()
    }

}
