package live.lingting.framework.elasticsearch.builder

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery
import co.elastic.clients.elasticsearch._types.query_dsl.Query
import java.util.function.Supplier
import live.lingting.framework.elasticsearch.ElasticsearchFunction
import live.lingting.framework.elasticsearch.composer.QueryComposer
import live.lingting.framework.elasticsearch.function.TermOperator
import live.lingting.framework.util.CollectionUtils

/**
 * @author lingting 2024-06-17 17:06
 */
class QueryBuilder<E> {
    private val must: MutableList<Query?> = ArrayList()

    private val mustNot: MutableList<Query?> = ArrayList()

    private val should: MutableList<Query?> = ArrayList()

    // region basic
    fun merge(builder: QueryBuilder<*>): QueryBuilder<E> {
        must.addAll(builder.must)
        mustNot.addAll(builder.mustNot)
        should.addAll(builder.should)
        return this
    }

    fun addMust(builder: QueryBuilder<*>): QueryBuilder<E> {
        return addMust(builder.build())
    }

    fun addMust(vararg queries: Query?): QueryBuilder<E> {
        return addMust(Arrays.asList(*queries))
    }

    fun addMust(queries: Collection<Query?>): QueryBuilder<E> {
        queries.stream().filter { obj: Query? -> Objects.nonNull(obj) }.forEach { e: Query? -> must.add(e) }
        return this
    }

    fun addMust(condition: Boolean, supplier: Supplier<Query?>): QueryBuilder<E> {
        if (condition) {
            must.add(supplier.get())
        }
        return this
    }

    fun addMustNot(builder: QueryBuilder<*>): QueryBuilder<E> {
        return addMustNot(builder.build())
    }

    fun addMustNot(vararg queries: Query?): QueryBuilder<E> {
        return addMustNot(Arrays.asList(*queries))
    }

    fun addMustNot(queries: Collection<Query?>): QueryBuilder<E> {
        queries.stream().filter { obj: Query? -> Objects.nonNull(obj) }.forEach { e: Query? -> mustNot.add(e) }
        return this
    }

    fun addShould(builder: QueryBuilder<*>): QueryBuilder<E> {
        return addShould(builder.build())
    }

    fun addShould(vararg queries: Query?): QueryBuilder<E> {
        return addShould(Arrays.asList(*queries))
    }

    fun addShould(queries: Collection<Query?>): QueryBuilder<E> {
        queries.stream().filter { obj: Query? -> Objects.nonNull(obj) }.forEach { e: Query? -> should.add(e) }
        return this
    }

    // endregion
    // region composer
    fun <T> term(field: String?, obj: T): QueryBuilder<E> {
        return addMust(QueryComposer.term<T>(field, obj))
    }

    fun <T> term(field: String?, obj: T, operator: TermOperator): QueryBuilder<E> {
        return addMust(QueryComposer.term<T>(field, obj, operator))
    }

    fun <T> terms(field: String?, objects: Collection<T>): QueryBuilder<E> {
        return addMust(QueryComposer.terms<T>(field, objects))
    }

    /**
     * 小于
     */
    fun <T> lt(field: String?, obj: T): QueryBuilder<E> {
        return addMust(QueryComposer.lt<T>(field, obj))
    }

    /**
     * 小于等于
     */
    fun <T> le(field: String?, obj: T): QueryBuilder<E> {
        return addMust(QueryComposer.le<T>(field, obj))
    }

    /**
     * 大于
     */
    fun <T> gt(field: String?, obj: T): QueryBuilder<E> {
        return addMust(QueryComposer.gt<T>(field, obj))
    }

    /**
     * 大于等于
     */
    fun <T> ge(field: String?, obj: T): QueryBuilder<E> {
        return addMust(QueryComposer.ge<T>(field, obj))
    }

    /**
     * 大于等于 start 小于等于 end
     */
    fun <T> between(field: String?, start: T, end: T): QueryBuilder<E> {
        return addMust(QueryComposer.between<T>(field, start, end))
    }

    fun exists(field: String?): QueryBuilder<E> {
        return addMust(QueryComposer.exists(field))
    }

    fun notExists(field: String?): QueryBuilder<E> {
        return addMust(QueryComposer.notExists(field))
    }

    fun should(vararg queries: Query?): QueryBuilder<E> {
        return addMust(QueryComposer.should(*queries))
    }

    fun should(queries: List<Query?>): QueryBuilder<E> {
        return addMust(QueryComposer.should(queries))
    }

    fun must(vararg queries: Query?): QueryBuilder<E> {
        return addMust(QueryComposer.must(*queries))
    }

    fun must(queries: List<Query?>): QueryBuilder<E> {
        return addMust(QueryComposer.must(queries))
    }

    fun <T> wildcardAll(field: String?, obj: T): QueryBuilder<E> {
        return addMust(QueryComposer.wildcardAll<T>(field, obj))
    }

    fun <T> wildcard(field: String?, obj: T): QueryBuilder<E> {
        return addMust(QueryComposer.wildcard<T>(field, obj))
    }

    fun not(vararg queries: Query?): QueryBuilder<E> {
        return addMust(QueryComposer.not(*queries))
    }

    fun not(queries: List<Query?>): QueryBuilder<E> {
        return addMust(QueryComposer.not(queries))
    }

    fun <T> term(func: ElasticsearchFunction<E, T>, obj: T): QueryBuilder<E> {
        return addMust(QueryComposer.term<T>(func, obj))
    }

    fun <T> term(func: ElasticsearchFunction<E, T>, obj: T, operator: TermOperator): QueryBuilder<E> {
        return addMust(QueryComposer.term<T>(func, obj, operator))
    }

    fun <T> terms(func: ElasticsearchFunction<E, T>, objects: Collection<T>): QueryBuilder<E> {
        return addMust(QueryComposer.terms<T>(func, objects))
    }

    /**
     * 小于
     */
    fun <T> lt(func: ElasticsearchFunction<E, T>, obj: T): QueryBuilder<E> {
        return addMust(QueryComposer.lt<T>(func, obj))
    }

    /**
     * 小于等于
     */
    fun <T> le(func: ElasticsearchFunction<E, T>, obj: T): QueryBuilder<E> {
        return addMust(QueryComposer.le<T>(func, obj))
    }

    /**
     * 大于
     */
    fun <T> gt(func: ElasticsearchFunction<E, T>, obj: T): QueryBuilder<E> {
        return addMust(QueryComposer.gt<T>(func, obj))
    }

    /**
     * 大于等于
     */
    fun <T> ge(func: ElasticsearchFunction<E, T>, obj: T): QueryBuilder<E> {
        return addMust(QueryComposer.ge<T>(func, obj))
    }

    /**
     * 大于等于 start 小于等于 end
     */
    fun <T> between(func: ElasticsearchFunction<E, T>, start: T, end: T): QueryBuilder<E> {
        return addMust(QueryComposer.between<T>(func, start, end))
    }

    fun <T> wildcardAll(func: ElasticsearchFunction<E, T>, obj: T): QueryBuilder<E> {
        return addMust(QueryComposer.wildcardAll<T>(func, obj))
    }

    fun <T> wildcard(func: ElasticsearchFunction<E, T>, obj: T): QueryBuilder<E> {
        return addMust(QueryComposer.wildcard<T>(func, obj))
    }

    // endregion
    // region composer ifPresent
    fun <T> termIfPresent(field: String?, obj: T): QueryBuilder<E> {
        return addMust(isPresent(obj)) { QueryComposer.term<T>(field, obj) }
    }

    fun <T> termIfPresent(field: String?, obj: T, operator: TermOperator): QueryBuilder<E> {
        return addMust(isPresent(obj)) { QueryComposer.term<T>(field, obj, operator) }
    }

    fun <T> termsIfPresent(field: String?, objects: Collection<T>): QueryBuilder<E> {
        return addMust(isPresent(objects)) { QueryComposer.terms<T>(field, objects) }
    }

    /**
     * 小于
     */
    fun <T> ltIfPresent(field: String?, obj: T): QueryBuilder<E> {
        return addMust(isPresent(obj)) { QueryComposer.lt<T>(field, obj) }
    }

    /**
     * 小于等于
     */
    fun <T> leIfPresent(field: String?, obj: T): QueryBuilder<E> {
        return addMust(isPresent(obj)) { QueryComposer.le<T>(field, obj) }
    }

    /**
     * 大于
     */
    fun <T> gtIfPresent(field: String?, obj: T): QueryBuilder<E> {
        return addMust(isPresent(obj)) { QueryComposer.gt<T>(field, obj) }
    }

    /**
     * 大于等于
     */
    fun <T> geIfPresent(field: String?, obj: T): QueryBuilder<E> {
        return addMust(isPresent(obj)) { QueryComposer.ge<T>(field, obj) }
    }

    /**
     * 大于等于 start 小于等于 end
     */
    fun <T> betweenIfPresent(field: String?, start: T, end: T): QueryBuilder<E> {
        return addMust(isPresent(start) && isPresent(end)) { QueryComposer.between<T>(field, start, end) }
    }

    fun <T> wildcardAllIfPresent(field: String?, obj: T): QueryBuilder<E> {
        return addMust(isPresent(obj)) { QueryComposer.wildcardAll<T>(field, obj) }
    }

    fun <T> wildcardIfPresent(field: String?, obj: T): QueryBuilder<E> {
        return addMust(isPresent(obj)) { QueryComposer.wildcard<T>(field, obj) }
    }

    fun <T> termIfPresent(func: ElasticsearchFunction<E, T>, obj: T): QueryBuilder<E> {
        return addMust(isPresent(obj)) { QueryComposer.term<T>(func, obj) }
    }

    fun <T> termIfPresent(func: ElasticsearchFunction<E, T>, obj: T, operator: TermOperator): QueryBuilder<E> {
        return addMust(isPresent(obj)) { QueryComposer.term<T>(func, obj, operator) }
    }

    fun <T> termsIfPresent(func: ElasticsearchFunction<E, T>, objects: Collection<T>): QueryBuilder<E> {
        return addMust(isPresent(objects)) { QueryComposer.terms<T>(func, objects) }
    }

    /**
     * 小于
     */
    fun <T> ltIfPresent(func: ElasticsearchFunction<E, T>, obj: T): QueryBuilder<E> {
        return addMust(isPresent(obj)) { QueryComposer.lt<T>(func, obj) }
    }

    /**
     * 小于等于
     */
    fun <T> leIfPresent(func: ElasticsearchFunction<E, T>, obj: T): QueryBuilder<E> {
        return addMust(isPresent(obj)) { QueryComposer.le<T>(func, obj) }
    }

    /**
     * 大于
     */
    fun <T> gtIfPresent(func: ElasticsearchFunction<E, T>, obj: T): QueryBuilder<E> {
        return addMust(isPresent(obj)) { QueryComposer.gt<T>(func, obj) }
    }

    /**
     * 大于等于
     */
    fun <T> geIfPresent(func: ElasticsearchFunction<E, T>, obj: T): QueryBuilder<E> {
        return addMust(isPresent(obj)) { QueryComposer.ge<T>(func, obj) }
    }

    /**
     * 大于等于 start 小于等于 end
     */
    fun <T> betweenIfPresent(func: ElasticsearchFunction<E, T>, start: T, end: T): QueryBuilder<E> {
        return addMust(isPresent(start) && isPresent(end)) { QueryComposer.between<T>(func, start, end) }
    }

    fun <T> wildcardAllIfPresent(func: ElasticsearchFunction<E, T>, obj: T): QueryBuilder<E> {
        return addMust(isPresent(obj)) { QueryComposer.wildcardAll<T>(func, obj) }
    }

    fun <T> wildcardIfPresent(func: ElasticsearchFunction<E, T>, obj: T): QueryBuilder<E> {
        return addMust(isPresent(obj)) { QueryComposer.wildcard<T>(func, obj) }
    }

    fun copy(): QueryBuilder<E> {
        return QueryBuilder<E>().merge(this)
    }

    fun <T> to(): QueryBuilder<T> {
        return QueryBuilder<T>().merge(this)
    }

    fun build(): Query {
        val builder = BoolQuery.Builder()
        if (!CollectionUtils.isEmpty(must)) {
            builder.must(ArrayList(must))
        }
        if (!CollectionUtils.isEmpty(should)) {
            builder.should(ArrayList(should))
        }
        if (!CollectionUtils.isEmpty(mustNot)) {
            builder.mustNot(ArrayList(mustNot))
        }

        val queryBuilder = Query.Builder()
        queryBuilder.bool(builder.build())
        return queryBuilder.build()
    }

    companion object {
        // endregion
        fun <C> builder(): QueryBuilder<C> {
            return QueryBuilder()
        }

        fun <C> builder(vararg queries: Query?): QueryBuilder<C> {
            return QueryBuilder<C>().addMust(*queries)
        }
    }
}
