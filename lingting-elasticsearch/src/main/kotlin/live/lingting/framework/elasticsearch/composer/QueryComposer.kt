package live.lingting.framework.elasticsearch.composer

import co.elastic.clients.elasticsearch._types.FieldValue
import co.elastic.clients.elasticsearch._types.query_dsl.Query
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery
import co.elastic.clients.json.JsonData
import co.elastic.clients.util.ObjectBuilder
import java.util.Objects
import java.util.function.Function
import live.lingting.framework.elasticsearch.EFunction
import live.lingting.framework.elasticsearch.ElasticsearchUtils
import live.lingting.framework.elasticsearch.function.TermOperator
import live.lingting.framework.util.CollectionUtils

/**
 * @author lingting 2024-03-06 17:33
 */
object QueryComposer {
    // region basic
    @JvmStatic
    fun <T> term(field: String, obj: T): Query {
        return term(field, obj) { builder -> builder }
    }

    @JvmStatic
    fun <T> term(field: String, obj: T, operator: Function<TermQuery.Builder, ObjectBuilder<TermQuery>>): Query {
        val value: FieldValue = ElasticsearchUtils.fieldValue(obj)
        return Query.of { qb ->
            qb.term { tq ->
                val builder = tq.field(field).value(value)
                operator.apply(builder)
            }
        }
    }

    @JvmStatic
    fun <T> terms(field: String, objects: Collection<T>): Query {
        val values: MutableList<FieldValue> = ArrayList()

        if (!CollectionUtils.isEmpty(objects)) {
            for (`object` in objects) {
                if (`object` == null) {
                    continue
                }
                val value: FieldValue = ElasticsearchUtils.fieldValue(`object`)
                values.add(value)
            }
        }

        return Query.of { qb -> qb.terms { tq -> tq.field(field).terms { tqf -> tqf.value(values) } } }
    }

    /**
     * 小于
     */
    @JvmStatic
    fun <T> lt(field: String, obj: T): Query {
        val value = JsonData.of(obj)
        return Query.of { qb -> qb.range { rb -> rb.untyped { ub -> ub.field(field).lt(value) } } }
    }

    /**
     * 小于等于
     */
    @JvmStatic
    fun <T> le(field: String, obj: T): Query {
        val value = JsonData.of(obj)
        return Query.of { qb -> qb.range { rq -> rq.untyped { ub -> ub.field(field).lte(value) } } }
    }

    /**
     * 大于
     */
    @JvmStatic
    fun <T> gt(field: String, obj: T): Query {
        val value = JsonData.of(obj)
        return Query.of { qb -> qb.range { rq -> rq.untyped { ub -> ub.field(field).gt(value) } } }
    }

    /**
     * 大于等于
     */
    @JvmStatic
    fun <T> ge(field: String, obj: T): Query {
        val value = JsonData.of(obj)
        return Query.of { qb -> qb.range { rq -> rq.untyped { ub -> ub.field(field).gte(value) } } }
    }

    /**
     * 大于等于 start 小于等于 end
     */
    @JvmStatic
    fun <T> between(field: String, start: T, end: T): Query {
        val startData = JsonData.of(start)
        val endData = JsonData.of(end)
        return Query.of { q ->
            q.range { rb ->
                rb.untyped { ub ->
                    ub.field(field) // 大于等于 start
                        .gte(startData) // 小于等于 end
                        .lte(endData)
                }
            }
        }
    }

    @JvmStatic
    fun exists(field: String): Query {
        return Query.of { q -> q.exists { e -> e.field(field) } }
    }

    @JvmStatic
    fun notExists(field: String): Query {
        return Query.of { q -> q.bool { b -> b.mustNot { mn -> mn.exists { e -> e.field(field) } } } }
    }

    @JvmStatic
    fun should(vararg queries: Query): Query {
        return should(queries.toList())
    }

    @JvmStatic
    fun should(queries: List<Query>): Query {
        return Query.of { q -> q.bool { b -> b.should(queries.stream().filter { obj -> Objects.nonNull(obj) }.toList()) } }
    }

    @JvmStatic
    fun must(vararg queries: Query): Query {
        return must(queries.toList())
    }

    @JvmStatic
    fun must(queries: List<Query>): Query {
        return Query.of { q -> q.bool { b -> b.must(queries.stream().filter { obj -> Objects.nonNull(obj) }.toList()) } }
    }

    @JvmStatic
    fun <T> wildcardAll(field: String, obj: T): Query {
        val format = String.format("*%s*", obj)
        return wildcard(field, format)
    }

    @JvmStatic
    fun <T> wildcard(field: String, obj: T): Query {
        val value = obj.toString()
        return Query.of { qb -> qb.wildcard { wq -> wq.field(field).value(value) } }
    }

    @JvmStatic
    fun not(vararg queries: Query): Query {
        return not(queries.toList())
    }

    @JvmStatic
    fun not(queries: List<Query>): Query {
        return Query.of { q -> q.bool { b -> b.mustNot(queries.stream().filter { obj -> Objects.nonNull(obj) }.toList()) } }
    }

    // endregion
    // region lambda
    @JvmStatic
    fun <T> term(func: EFunction<*, T>, obj: T): Query {
        return term(func, obj) { it }
    }

    @JvmStatic
    fun <T> term(func: EFunction<*, T>, obj: T, operator: TermOperator): Query {
        val field: String = ElasticsearchUtils.fieldName(func)
        return term(field, obj, operator)
    }

    @JvmStatic
    fun <T> terms(func: EFunction<*, T>, objects: Collection<T>): Query {
        val field: String = ElasticsearchUtils.fieldName(func)
        return terms<T>(field, objects)
    }

    /**
     * 小于
     */
    @JvmStatic
    fun <T> lt(func: EFunction<*, T>, obj: T): Query {
        val field: String = ElasticsearchUtils.fieldName(func)
        return lt(field, obj)
    }

    /**
     * 小于等于
     */
    @JvmStatic
    fun <T> le(func: EFunction<*, T>, obj: T): Query {
        val field: String = ElasticsearchUtils.fieldName(func)
        return le(field, obj)
    }

    /**
     * 大于
     */
    @JvmStatic
    fun <T> gt(func: EFunction<*, T>, obj: T): Query {
        val field: String = ElasticsearchUtils.fieldName(func)
        return gt(field, obj)
    }

    /**
     * 大于等于
     */
    @JvmStatic
    fun <T> ge(func: EFunction<*, T>, obj: T): Query {
        val field: String = ElasticsearchUtils.fieldName(func)
        return ge(field, obj)
    }

    /**
     * 大于等于 start 小于等于 end
     */
    @JvmStatic
    fun <T> between(func: EFunction<*, T>, start: T, end: T): Query {
        val field: String = ElasticsearchUtils.fieldName(func)
        return between(field, start, end)
    }

    @JvmStatic
    fun <T> wildcardAll(func: EFunction<*, T>, obj: T): Query {
        val field: String = ElasticsearchUtils.fieldName(func)
        return wildcardAll(field, obj)
    }

    @JvmStatic
    fun <T> wildcard(func: EFunction<*, T>, obj: T): Query {
        val field: String = ElasticsearchUtils.fieldName(func)
        return wildcard(field, obj)
    } // endregion
}

