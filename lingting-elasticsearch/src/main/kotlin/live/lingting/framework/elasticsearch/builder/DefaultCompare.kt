package live.lingting.framework.elasticsearch.builder

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery
import co.elastic.clients.elasticsearch._types.query_dsl.Query

/**
 * @author lingting 2025/1/21 15:12
 */
@Suppress("UNCHECKED_CAST")
open class DefaultCompare<E> : Compare<E, DefaultCompare<E>> {

    protected val must = ArrayList<Query>()

    protected val mustNot = ArrayList<Query>()

    protected val should = ArrayList<Query>()

    override fun merge(builder: Compare<*, *>): DefaultCompare<E> {
        if (builder is DefaultCompare) {
            addMust(builder.must)
            addMustNot(builder.mustNot)
            addShould(builder.should)
        } else {
            must.add(builder.buildQuery())
        }
        return this
    }

    override fun addMust(queries: Collection<Query>): DefaultCompare<E> {
        must.addAll(queries)
        return this
    }

    override fun addMustNot(queries: Collection<Query>): DefaultCompare<E> {
        mustNot.addAll(queries)
        return this
    }

    override fun addShould(queries: Collection<Query>): DefaultCompare<E> {
        should.addAll(queries)
        return this
    }

    override fun buildQuery(): Query {
        val boolean = BoolQuery.Builder()
        if (must.isNotEmpty()) {
            boolean.must(ArrayList(must))
        }

        if (should.isNotEmpty()) {
            boolean.should(ArrayList(should))
        }

        if (mustNot.isNotEmpty()) {
            boolean.mustNot(ArrayList(mustNot))
        }

        val builder = Query.Builder()
        builder.bool(boolean.build())
        return builder.build()
    }


}
