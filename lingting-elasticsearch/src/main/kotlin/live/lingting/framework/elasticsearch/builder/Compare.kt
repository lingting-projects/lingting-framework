package live.lingting.framework.elasticsearch.builder

import co.elastic.clients.elasticsearch._types.query_dsl.Query
import java.util.function.Supplier
import live.lingting.framework.elasticsearch.EFunction
import live.lingting.framework.elasticsearch.function.TermOperator
import live.lingting.framework.elasticsearch.util.QueryUtils
import live.lingting.framework.util.ValueUtils.isPresent

/**
 * @author lingting 2025/1/21 15:12
 */
@Suppress("UNCHECKED_CAST")
interface Compare<E, B : Compare<E, B>> {

    // region basic

    fun merge(builder: Compare<*, *>): B {
        builder.addMust(builder.buildQuery())
        return this as B
    }

    fun addMust(builder: Compare<*, *>): B {
        return addMust(builder.buildQuery())
    }

    fun addMust(vararg queries: Query): B {
        return addMust(queries.toList())
    }

    fun addMust(queries: Collection<Query>): B

    fun addMust(condition: Boolean, supplier: Supplier<Query>): B {
        if (condition) {
            val query = supplier.get()
            addMust(query)
        }
        return this as B
    }

    fun addMustNot(builder: Compare<*, *>): B {
        return addMustNot(builder.buildQuery())
    }

    fun addMustNot(vararg queries: Query): B {
        return addMustNot(queries.toList())
    }

    fun addMustNot(queries: Collection<Query>): B

    fun addMustNot(condition: Boolean, supplier: Supplier<Query>): B {
        if (condition) {
            val query = supplier.get()
            addMustNot(query)
        }
        return this as B
    }

    fun addShould(builder: Compare<*, *>): B {
        return addShould(builder.buildQuery())
    }

    fun addShould(vararg queries: Query): B {
        return addShould(queries.toList())
    }

    fun addShould(queries: Collection<Query>): B

    fun addShould(condition: Boolean, supplier: Supplier<Query>): B {
        if (condition) {
            val query = supplier.get()
            addShould(query)
        }
        return this as B
    }

    fun buildQuery(): Query

    // endregion

    // region composer
    fun <T> term(field: String, obj: T?): B {
        return addMust(QueryUtils.term<T>(field, obj))
    }

    fun <T> term(field: String, obj: T?, operator: TermOperator): B {
        return addMust(QueryUtils.term<T>(field, obj, operator))
    }

    fun <T> terms(field: String, objects: Collection<T>?): B {
        return addMust(QueryUtils.terms<T>(field, objects))
    }

    /**
     * 小于
     */
    fun <T> lt(field: String, obj: T?): B {
        return addMust(QueryUtils.lt<T>(field, obj))
    }

    /**
     * 小于等于
     */
    fun <T> le(field: String, obj: T?): B {
        return addMust(QueryUtils.le<T>(field, obj))
    }

    /**
     * 大于
     */
    fun <T> gt(field: String, obj: T?): B {
        return addMust(QueryUtils.gt<T>(field, obj))
    }

    /**
     * 大于等于
     */
    fun <T> ge(field: String, obj: T?): B {
        return addMust(QueryUtils.ge<T>(field, obj))
    }

    /**
     * 大于等于 start 小于等于 end
     */
    fun <T> between(field: String, start: T, end: T): B {
        return addMust(QueryUtils.between<T>(field, start, end))
    }

    fun exists(field: String): B {
        return addMust(QueryUtils.exists(field))
    }

    fun notExists(field: String): B {
        return addMust(QueryUtils.notExists(field))
    }

    fun should(vararg queries: Query): B {
        return addMust(QueryUtils.should(*queries))
    }

    fun should(queries: List<Query>): B {
        return addMust(QueryUtils.should(queries))
    }

    fun must(vararg queries: Query): B {
        return addMust(QueryUtils.must(*queries))
    }

    fun must(queries: List<Query>): B {
        return addMust(QueryUtils.must(queries))
    }

    fun <T> wildcardAll(field: String, obj: T?): B {
        return addMust(QueryUtils.wildcardAll<T>(field, obj))
    }

    fun <T> wildcard(field: String, obj: T?): B {
        return addMust(QueryUtils.wildcard<T>(field, obj))
    }

    fun not(vararg queries: Query): B {
        return addMust(QueryUtils.not(*queries))
    }

    fun not(queries: List<Query>): B {
        return addMust(QueryUtils.not(queries))
    }

    fun <T> term(func: EFunction<E, T>, obj: T?): B {
        return addMust(QueryUtils.term<T>(func, obj))
    }

    fun <T> term(func: EFunction<E, T>, obj: T?, operator: TermOperator): B {
        return addMust(QueryUtils.term<T>(func, obj, operator))
    }

    fun <T> terms(func: EFunction<E, T>, objects: Collection<T>?): B {
        return addMust(QueryUtils.terms<T>(func, objects))
    }

    /**
     * 小于
     */
    fun <T> lt(func: EFunction<E, T>, obj: T?): B {
        return addMust(QueryUtils.lt<T>(func, obj))
    }

    /**
     * 小于等于
     */
    fun <T> le(func: EFunction<E, T>, obj: T?): B {
        return addMust(QueryUtils.le<T>(func, obj))
    }

    /**
     * 大于
     */
    fun <T> gt(func: EFunction<E, T>, obj: T?): B {
        return addMust(QueryUtils.gt<T>(func, obj))
    }

    /**
     * 大于等于
     */
    fun <T> ge(func: EFunction<E, T>, obj: T?): B {
        return addMust(QueryUtils.ge<T>(func, obj))
    }

    /**
     * 大于等于 start 小于等于 end
     */
    fun <T> between(func: EFunction<E, T>, start: T, end: T): B {
        return addMust(QueryUtils.between<T>(func, start, end))
    }

    fun <T> wildcardAll(func: EFunction<E, T>, obj: T?): B {
        return addMust(QueryUtils.wildcardAll<T>(func, obj))
    }

    fun <T> wildcard(func: EFunction<E, T>, obj: T?): B {
        return addMust(QueryUtils.wildcard<T>(func, obj))
    }

    // endregion

    // region composer ifPresent
    fun <T> termIfPresent(field: String, obj: T?): B {
        return addMust(isPresent(obj)) { QueryUtils.term<T>(field, obj) }
    }

    fun <T> termIfPresent(field: String, obj: T?, operator: TermOperator): B {
        return addMust(isPresent(obj)) { QueryUtils.term<T>(field, obj, operator) }
    }

    fun <T> termsIfPresent(field: String, objects: Collection<T>?): B {
        return addMust(isPresent(objects)) { QueryUtils.terms<T>(field, objects) }
    }

    /**
     * 小于
     */
    fun <T> ltIfPresent(field: String, obj: T?): B {
        return addMust(isPresent(obj)) { QueryUtils.lt<T>(field, obj) }
    }

    /**
     * 小于等于
     */
    fun <T> leIfPresent(field: String, obj: T?): B {
        return addMust(isPresent(obj)) { QueryUtils.le<T>(field, obj) }
    }

    /**
     * 大于
     */
    fun <T> gtIfPresent(field: String, obj: T?): B {
        return addMust(isPresent(obj)) { QueryUtils.gt<T>(field, obj) }
    }

    /**
     * 大于等于
     */
    fun <T> geIfPresent(field: String, obj: T?): B {
        return addMust(isPresent(obj)) { QueryUtils.ge<T>(field, obj) }
    }

    /**
     * 大于等于 start 小于等于 end
     */
    fun <T> betweenIfPresent(field: String, start: T, end: T): B {
        return addMust(isPresent(start) && isPresent(end)) { QueryUtils.between<T>(field, start, end) }
    }

    fun <T> wildcardAllIfPresent(field: String, obj: T?): B {
        return addMust(isPresent(obj)) { QueryUtils.wildcardAll<T>(field, obj) }
    }

    fun <T> wildcardIfPresent(field: String, obj: T?): B {
        return addMust(isPresent(obj)) { QueryUtils.wildcard<T>(field, obj) }
    }

    fun <T> termIfPresent(func: EFunction<E, T>, obj: T?): B {
        return addMust(isPresent(obj)) { QueryUtils.term<T>(func, obj) }
    }

    fun <T> termIfPresent(func: EFunction<E, T>, obj: T?, operator: TermOperator): B {
        return addMust(isPresent(obj)) { QueryUtils.term<T>(func, obj, operator) }
    }

    fun <T> termsIfPresent(func: EFunction<E, T>, objects: Collection<T>?): B {
        return addMust(isPresent(objects)) { QueryUtils.terms<T>(func, objects) }
    }

    /**
     * 小于
     */
    fun <T> ltIfPresent(func: EFunction<E, T>, obj: T?): B {
        return addMust(isPresent(obj)) { QueryUtils.lt<T>(func, obj) }
    }

    /**
     * 小于等于
     */
    fun <T> leIfPresent(func: EFunction<E, T>, obj: T?): B {
        return addMust(isPresent(obj)) { QueryUtils.le<T>(func, obj) }
    }

    /**
     * 大于
     */
    fun <T> gtIfPresent(func: EFunction<E, T>, obj: T?): B {
        return addMust(isPresent(obj)) { QueryUtils.gt<T>(func, obj) }
    }

    /**
     * 大于等于
     */
    fun <T> geIfPresent(func: EFunction<E, T>, obj: T?): B {
        return addMust(isPresent(obj)) { QueryUtils.ge<T>(func, obj) }
    }

    /**
     * 大于等于 start 小于等于 end
     */
    fun <T> betweenIfPresent(func: EFunction<E, T>, start: T, end: T): B {
        return addMust(isPresent(start) && isPresent(end)) { QueryUtils.between<T>(func, start, end) }
    }

    fun <T> wildcardAllIfPresent(func: EFunction<E, T>, obj: T?): B {
        return addMust(isPresent(obj)) { QueryUtils.wildcardAll<T>(func, obj) }
    }

    fun <T> wildcardIfPresent(func: EFunction<E, T>, obj: T?): B {
        return addMust(isPresent(obj)) { QueryUtils.wildcard<T>(func, obj) }
    }

    // endregion

}
