package live.lingting.framework.elasticsearch.function

import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery
import co.elastic.clients.util.ObjectBuilder
import java.util.function.Function

/**
 * @author lingting 2024-06-19 10:13
 */
interface TermOperator : Function<TermQuery.Builder?, ObjectBuilder<TermQuery?>?>
