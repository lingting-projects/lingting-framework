package live.lingting.framework.elasticsearch.composer

import co.elastic.clients.elasticsearch._types.FieldValue
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery
import co.elastic.clients.elasticsearch._types.query_dsl.ExistsQuery
import co.elastic.clients.elasticsearch._types.query_dsl.Query
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery
import co.elastic.clients.elasticsearch._types.query_dsl.TermsQuery
import co.elastic.clients.elasticsearch._types.query_dsl.TermsQueryField
import co.elastic.clients.elasticsearch._types.query_dsl.UntypedRangeQuery
import co.elastic.clients.elasticsearch._types.query_dsl.WildcardQuery
import co.elastic.clients.json.JsonData
import co.elastic.clients.util.ObjectBuilder
import live.lingting.framework.elasticsearch.ElasticsearchFunction
import live.lingting.framework.elasticsearch.ElasticsearchUtils
import live.lingting.framework.elasticsearch.function.TermOperator
import live.lingting.framework.util.CollectionUtils
import java.util.*
import java.util.function.Function

/**
 * @author lingting 2024-03-06 17:33
 */
class QueryComposer private constructor() {
    init {
        throw UnsupportedOperationException("This is a utility class and cannot be instantiated")
    }

    companion object {
        // region basic
        fun <T> term(field: String?, obj: T): Query {
            return term(field, obj) { builder: TermQuery.Builder? -> builder }
        }

        fun <T> term(field: String?, obj: T, operator: Function<TermQuery.Builder?, ObjectBuilder<TermQuery>?>): Query {
            val value: FieldValue = ElasticsearchUtils.Companion.fieldValue(obj)
            return Query.of { qb: Query.Builder ->
                qb.term { tq: TermQuery.Builder ->
                    val builder = tq.field(field).value(value)
                    operator.apply(builder)
                }
            }
        }

        fun <T> terms(field: String?, objects: Collection<T?>): Query {
            val values: MutableList<FieldValue> = ArrayList()

            if (!CollectionUtils.isEmpty(objects)) {
                for (`object` in objects) {
                    if (`object` == null) {
                        continue
                    }
                    val value: FieldValue = ElasticsearchUtils.Companion.fieldValue(`object`)
                    values.add(value)
                }
            }

            return Query.of { qb: Query.Builder -> qb.terms { tq: TermsQuery.Builder -> tq.field(field).terms { tqf: TermsQueryField.Builder -> tqf.value(values) } } }
        }

        /**
         * 小于
         */
        fun <T> lt(field: String?, obj: T): Query {
            val value = JsonData.of(obj)
            return Query.of { qb: Query.Builder -> qb.range { rb: RangeQuery.Builder -> rb.untyped { ub: UntypedRangeQuery.Builder -> ub.field(field).lt(value) } } }
        }

        /**
         * 小于等于
         */
        fun <T> le(field: String?, obj: T): Query {
            val value = JsonData.of(obj)
            return Query.of { qb: Query.Builder -> qb.range { rq: RangeQuery.Builder -> rq.untyped { ub: UntypedRangeQuery.Builder -> ub.field(field).lte(value) } } }
        }

        /**
         * 大于
         */
        fun <T> gt(field: String?, obj: T): Query {
            val value = JsonData.of(obj)
            return Query.of { qb: Query.Builder -> qb.range { rq: RangeQuery.Builder -> rq.untyped { ub: UntypedRangeQuery.Builder -> ub.field(field).gt(value) } } }
        }

        /**
         * 大于等于
         */
        fun <T> ge(field: String?, obj: T): Query {
            val value = JsonData.of(obj)
            return Query.of { qb: Query.Builder -> qb.range { rq: RangeQuery.Builder -> rq.untyped { ub: UntypedRangeQuery.Builder -> ub.field(field).gte(value) } } }
        }

        /**
         * 大于等于 start 小于等于 end
         */
        fun <T> between(field: String?, start: T, end: T): Query {
            val startData = JsonData.of(start)
            val endData = JsonData.of(end)
            return Query.of { q: Query.Builder ->
                q.range { rb: RangeQuery.Builder ->
                    rb.untyped { ub: UntypedRangeQuery.Builder ->
                        ub.field(field) // 大于等于 start
                            .gte(startData) // 小于等于 end
                            .lte(endData)
                    }
                }
            }
        }

        fun exists(field: String?): Query {
            return Query.of { q: Query.Builder -> q.exists { e: ExistsQuery.Builder -> e.field(field) } }
        }

        fun notExists(field: String?): Query {
            return Query.of { q: Query.Builder -> q.bool { b: BoolQuery.Builder -> b.mustNot { mn: Query.Builder -> mn.exists { e: ExistsQuery.Builder -> e.field(field) } } } }
        }

        fun should(vararg queries: Query?): Query {
            return should(Arrays.asList(*queries))
        }

        fun should(queries: List<Query?>): Query {
            return Query.of { q: Query.Builder -> q.bool { b: BoolQuery.Builder -> b.should(queries.stream().filter { obj: Query? -> Objects.nonNull(obj) }.toList()) } }
        }

        fun must(vararg queries: Query?): Query {
            return must(Arrays.asList(*queries))
        }

        fun must(queries: List<Query?>): Query {
            return Query.of { q: Query.Builder -> q.bool { b: BoolQuery.Builder -> b.must(queries.stream().filter { obj: Query? -> Objects.nonNull(obj) }.toList()) } }
        }

        fun <T> wildcardAll(field: String?, obj: T): Query {
            val format = String.format("*%s*", obj)
            return wildcard(field, format)
        }

        fun <T> wildcard(field: String?, obj: T): Query {
            val value = obj.toString()
            return Query.of { qb: Query.Builder -> qb.wildcard { wq: WildcardQuery.Builder -> wq.field(field).value(value) } }
        }

        fun not(vararg queries: Query?): Query {
            return not(Arrays.asList(*queries))
        }

        fun not(queries: List<Query?>): Query {
            return Query.of { q: Query.Builder -> q.bool { b: BoolQuery.Builder -> b.mustNot(queries.stream().filter { obj: Query? -> Objects.nonNull(obj) }.toList()) } }
        }

        // endregion
        // region lambda
        fun <T> term(func: ElasticsearchFunction<*, T>, obj: T): Query {
            return term(func, obj) { builder: TermQuery.Builder? -> builder }
        }

        fun <T> term(func: ElasticsearchFunction<*, T>, obj: T, operator: TermOperator): Query {
            val field: String = ElasticsearchUtils.Companion.fieldName(func)
            return term(field, obj, operator)
        }

        fun <T> terms(func: ElasticsearchFunction<*, T>, objects: Collection<T>): Query {
            val field: String = ElasticsearchUtils.Companion.fieldName(func)
            return terms<T>(field, objects)
        }

        /**
         * 小于
         */
        fun <T> lt(func: ElasticsearchFunction<*, T>, obj: T): Query {
            val field: String = ElasticsearchUtils.Companion.fieldName(func)
            return lt(field, obj)
        }

        /**
         * 小于等于
         */
        fun <T> le(func: ElasticsearchFunction<*, T>, obj: T): Query {
            val field: String = ElasticsearchUtils.Companion.fieldName(func)
            return le(field, obj)
        }

        /**
         * 大于
         */
        fun <T> gt(func: ElasticsearchFunction<*, T>, obj: T): Query {
            val field: String = ElasticsearchUtils.Companion.fieldName(func)
            return gt(field, obj)
        }

        /**
         * 大于等于
         */
        fun <T> ge(func: ElasticsearchFunction<*, T>, obj: T): Query {
            val field: String = ElasticsearchUtils.Companion.fieldName(func)
            return ge(field, obj)
        }

        /**
         * 大于等于 start 小于等于 end
         */
        fun <T> between(func: ElasticsearchFunction<*, T>, start: T, end: T): Query {
            val field: String = ElasticsearchUtils.Companion.fieldName(func)
            return between(field, start, end)
        }

        fun <T> wildcardAll(func: ElasticsearchFunction<*, T>, obj: T): Query {
            val field: String = ElasticsearchUtils.Companion.fieldName(func)
            return wildcardAll(field, obj)
        }

        fun <T> wildcard(func: ElasticsearchFunction<*, T>, obj: T): Query {
            val field: String = ElasticsearchUtils.Companion.fieldName(func)
            return wildcard(field, obj)
        } // endregion
    }
}
