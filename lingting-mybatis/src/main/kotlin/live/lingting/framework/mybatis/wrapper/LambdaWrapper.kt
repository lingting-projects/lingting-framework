package live.lingting.framework.mybatis.wrapper

import com.baomidou.mybatisplus.core.toolkit.StringUtils.checkValNotNull
import com.baomidou.mybatisplus.core.toolkit.support.ColumnCache
import com.baomidou.mybatisplus.core.toolkit.support.SFunction
import java.util.function.BiPredicate
import java.util.function.Consumer

abstract class LambdaWrapper<T : Any, C : LambdaWrapper<T, C>> : AbstractWrapper<T, C>() {

    fun column(sf: SFunction<T, *>): ColumnCache {
        return Wrappers.column(sf)
    }

    fun columns(sfs: Collection<SFunction<T, *>>) = sfs.map(this::column)

    fun field(sf: SFunction<T, *>): String {
        val column = Wrappers.column(sf)
        val name = column.columnSelect
        return field(name)
    }

    fun fields(collection: Collection<SFunction<T, *>?>): MutableList<String> {
        return collection.filterNotNull().map(this::field).toMutableList()
    }

    fun fields(array: Array<SFunction<T, *>?>): MutableList<String> {
        return fields(array.toList())
    }

    // region compare
    fun allEqLambda(params: Map<SFunction<T, Any?>, Any?>): C {
        return allEqLambda(params, true)
    }

    fun allEqLambda(params: Map<SFunction<T, Any?>, Any?>, null2IsNull: Boolean): C {
        return allEqLambda(true, params, null2IsNull)
    }

    fun allEqLambda(condition: Boolean, params: Map<SFunction<T, Any?>, Any?>, null2IsNull: Boolean): C {
        if (condition && params.isNotEmpty()) {
            params.forEach { (k, v) ->
                if (checkValNotNull(v)) {
                    eq(k, v!!)
                } else {
                    if (null2IsNull) {
                        isNull(k)
                    }
                }
            }
        }
        return c
    }

    fun allEqLambda(filter: BiPredicate<SFunction<T, Any?>, Any?>, params: Map<SFunction<T, Any?>, Any?>): C {
        return allEqLambda(filter, params, true)
    }

    fun allEqLambda(
        filter: BiPredicate<SFunction<T, Any?>, Any?>, params: Map<SFunction<T, Any?>, Any?>,
        null2IsNull: Boolean
    ): C {
        return allEqLambda(true, filter, params, null2IsNull)
    }

    fun allEqLambda(
        condition: Boolean, filter: BiPredicate<SFunction<T, Any?>, Any?>, params: Map<SFunction<T, Any?>, Any?>,
        null2IsNull: Boolean
    ): C {
        if (condition && params.isNotEmpty()) {
            params.forEach { (k, v) ->
                if (filter.test(k, v)) {
                    if (checkValNotNull(v)) {
                        eq(k, v!!)
                    } else {
                        if (null2IsNull) {
                            isNull(k)
                        }
                    }
                }
            }
        }
        return c
    }

    fun <V : Any> eq(column: SFunction<T, V?>, value: V?): C {
        return eq(true, column, value)
    }

    fun <V : Any> eq(condition: Boolean, column: SFunction<T, V?>, value: V?): C {
        if (!condition) {
            return c
        }
        val field: String = field(column)
        return eq(field, value)
    }

    fun <V : Any> ne(column: SFunction<T, V?>, value: V?): C {
        return ne(true, column, value)
    }

    fun <V : Any> ne(condition: Boolean, column: SFunction<T, V?>, value: V?): C {
        if (!condition) {
            return c
        }
        val field: String = field(column)
        return ne(field, value)
    }

    fun <V : Any> gt(column: SFunction<T, V?>, value: V?): C {
        return gt(true, column, value)
    }

    fun <V : Any> gt(condition: Boolean, column: SFunction<T, V?>, value: V?): C {
        if (!condition) {
            return c
        }
        val field: String = field(column)
        return gt(field, value)
    }

    fun <V : Any> ge(column: SFunction<T, V?>, value: V?): C {
        return ge(true, column, value)
    }

    fun <V : Any> ge(condition: Boolean, column: SFunction<T, V?>, value: V?): C {
        if (!condition) {
            return c
        }
        val field: String = field(column)
        return ge(field, value)
    }

    fun <V : Any> lt(column: SFunction<T, V?>, value: V?): C {
        return lt(true, column, value)
    }

    fun <V : Any> lt(condition: Boolean, column: SFunction<T, V?>, value: V?): C {
        if (!condition) {
            return c
        }
        val field: String = field(column)
        return lt(field, value)
    }

    fun <V : Any> le(column: SFunction<T, V?>, value: V?): C {
        return le(true, column, value)
    }

    fun <V : Any> le(condition: Boolean, column: SFunction<T, V?>, value: V?): C {
        if (!condition) {
            return c
        }
        val field: String = field(column)
        return le(field, value)
    }

    fun <V : Any> between(column: SFunction<T, V?>, val1: V?, val2: V?): C {
        return between(true, column, val1, val2)
    }

    fun <V : Any> between(condition: Boolean, column: SFunction<T, V?>, val1: V?, val2: Any?): C {
        if (!condition) {
            return c
        }
        val field: String = field(column)
        return between(field, val1, val2)
    }

    fun <V : Any> notBetween(column: SFunction<T, V?>, val1: V?, val2: V?): C {
        return notBetween(true, column, val1, val2)
    }

    fun <V : Any> notBetween(condition: Boolean, column: SFunction<T, V?>, val1: V?, val2: Any?): C {
        if (!condition) {
            return c
        }
        val field: String = field(column)
        return notBetween(field, val1, val2)
    }

    fun <V : Any> like(column: SFunction<T, V?>, value: V?): C {
        return like(true, column, value)
    }

    fun <V : Any> like(condition: Boolean, column: SFunction<T, V?>, value: V?): C {
        if (!condition) {
            return c
        }
        val field: String = field(column)
        return like(field, value)
    }

    fun <V : Any> notLike(column: SFunction<T, V?>, value: V?): C {
        return notLike(true, column, value)
    }

    fun <V : Any> notLike(condition: Boolean, column: SFunction<T, V?>, value: V?): C {
        if (!condition) {
            return c
        }
        val field: String = field(column)
        return notLike(field, value)
    }

    fun <V : Any> notLikeLeft(column: SFunction<T, V?>, value: V?): C {
        return notLikeLeft(true, column, value)
    }

    fun <V : Any> notLikeLeft(condition: Boolean, column: SFunction<T, V?>, value: V?): C {
        if (!condition) {
            return c
        }
        val field: String = field(column)
        return notLikeLeft(field, value)
    }

    fun <V : Any> notLikeRight(column: SFunction<T, V?>, value: V?): C {
        return notLikeRight(true, column, value)
    }

    fun <V : Any> notLikeRight(condition: Boolean, column: SFunction<T, V?>, value: V?): C {
        if (!condition) {
            return c
        }
        val field: String = field(column)
        return notLikeRight(field, value)
    }

    fun <V : Any> likeLeft(column: SFunction<T, V?>, value: V?): C {
        return likeLeft(true, column, value)
    }

    fun <V : Any> likeLeft(condition: Boolean, column: SFunction<T, V?>, value: V?): C {
        if (!condition) {
            return c
        }
        val field: String = field(column)
        return likeLeft(field, value)
    }

    fun <V : Any> likeRight(column: SFunction<T, V?>, value: V?): C {
        return likeRight(true, column, value)
    }

    fun <V : Any> likeRight(condition: Boolean, column: SFunction<T, V?>, value: V?): C {
        if (!condition) {
            return c
        }
        val field: String = field(column)
        return likeRight(field, value)
    }

    // endregion

    // region compare ifPresent
    fun <V : Any> eqIfPresent(column: SFunction<T, V?>, value: V?): C {
        return eq(isPresent(value), column, value)
    }

    fun <V : Any> neIfPresent(column: SFunction<T, V?>, value: V?): C {
        return ne(isPresent(value), column, value)
    }

    fun <V : Any> gtIfPresent(column: SFunction<T, V?>, value: V?): C {
        return gt(isPresent(value), column, value)
    }

    fun <V : Any> geIfPresent(column: SFunction<T, V?>, value: V?): C {
        return ge(isPresent(value), column, value)
    }

    fun <V : Any> ltIfPresent(column: SFunction<T, V?>, value: V?): C {
        return lt(isPresent(value), column, value)
    }

    fun <V : Any> leIfPresent(column: SFunction<T, V?>, value: V?): C {
        return le(isPresent(value), column, value)
    }

    fun <V : Any> betweenIfPresent(column: SFunction<T, V?>, val1: V?, val2: V?): C {
        return between(isPresent(val1) && isPresent(val2), column, val1, val2)
    }

    fun <V : Any> notBetweenIfPresent(column: SFunction<T, V?>, val1: V?, val2: V?): C {
        return notBetween(isPresent(val1) && isPresent(val2), column, val1, val2)
    }

    fun <V : Any> likeIfPresent(column: SFunction<T, V?>, value: V?): C {
        return like(isPresent(value), column, value)
    }

    fun <V : Any> notLikeIfPresent(column: SFunction<T, V?>, value: V?): C {
        return notLike(isPresent(value), column, value)
    }

    fun <V : Any> notLikeLeftIfPresent(column: SFunction<T, V?>, value: V?): C {
        return notLikeLeft(isPresent(value), column, value)
    }

    fun <V : Any> notLikeRightIfPresent(column: SFunction<T, V?>, value: V?): C {
        return notLikeRight(isPresent(value), column, value)
    }

    fun <V : Any> likeLeftIfPresent(column: SFunction<T, V?>, value: V?): C {
        return likeLeft(isPresent(value), column, value)
    }

    fun <V : Any> likeRightIfPresent(column: SFunction<T, V?>, value: V?): C {
        return likeRight(isPresent(value), column, value)
    }

    // endregion

    // region compare extended
    // endregion

    // region func
    fun isNull(column: SFunction<T, *>): C {
        return isNull(true, column)
    }

    fun isNull(condition: Boolean, column: SFunction<T, *>): C {
        if (!condition) {
            return c
        }
        val field: String = field(column)
        return isNull(field)
    }

    fun isNotNull(column: SFunction<T, *>): C {
        return isNotNull(true, column)
    }

    fun isNotNull(condition: Boolean, column: SFunction<T, *>): C {
        if (!condition) {
            return c
        }
        val field: String = field(column)
        return isNotNull(field)
    }

    fun <V : Any> `in`(column: SFunction<T, V?>, coll: Collection<V>): C {
        return `in`(true, column, coll)
    }

    fun <V : Any> `in`(condition: Boolean, column: SFunction<T, V?>, coll: Collection<V>): C {
        if (!condition) {
            return c
        }
        val field: String = field(column)
        return `in`(field, coll)
    }

    fun <V : Any> `in`(column: SFunction<T, V?>, vararg values: V): C {
        return `in`(true, column, *values)
    }

    fun <V : Any> `in`(condition: Boolean, column: SFunction<T, V?>, vararg values: V): C {
        if (!condition) {
            return c
        }
        val field: String = field(column)
        return `in`(field, *values)
    }

    fun <V : Any> notIn(column: SFunction<T, V?>, coll: Collection<V>): C {
        return notIn(true, column, coll)
    }

    fun <V : Any> notIn(condition: Boolean, column: SFunction<T, V?>, coll: Collection<V>): C {
        if (!condition) {
            return c
        }
        val field: String = field(column)
        return notIn(field, coll)
    }

    fun <V : Any> notIn(column: SFunction<T, V?>, vararg value: V): C {
        return notIn(true, column, *value)
    }

    fun <V : Any> notIn(condition: Boolean, column: SFunction<T, V?>, vararg values: V): C {
        if (!condition) {
            return c
        }
        val field: String = field(column)
        return notIn(field, *values)
    }

    fun inSql(column: SFunction<T, *>, inValue: String): C {
        return inSql(true, column, inValue)
    }

    fun inSql(condition: Boolean, column: SFunction<T, *>, inValue: String): C {
        if (!condition) {
            return c
        }
        val field: String = field(column)
        return inSql(field, inValue)
    }

    fun gtSql(condition: Boolean, column: SFunction<T, *>, inValue: String): C {
        if (!condition) {
            return c
        }
        val field: String = field(column)
        return gtSql(field, inValue)
    }

    fun gtSql(column: SFunction<T, *>, inValue: String): C {
        return gtSql(true, column, inValue)
    }

    fun geSql(condition: Boolean, column: SFunction<T, *>, inValue: String): C {
        if (!condition) {
            return c
        }
        val field: String = field(column)
        return geSql(field, inValue)
    }

    fun geSql(column: SFunction<T, *>, inValue: String): C {
        return geSql(true, column, inValue)
    }

    fun ltSql(condition: Boolean, column: SFunction<T, *>, inValue: String): C {
        if (!condition) {
            return c
        }
        val field: String = field(column)
        return ltSql(field, inValue)
    }

    fun ltSql(column: SFunction<T, *>, inValue: String): C {
        return ltSql(true, column, inValue)
    }

    fun leSql(condition: Boolean, column: SFunction<T, *>, inValue: String): C {
        if (!condition) {
            return c
        }
        val field: String = field(column)
        return leSql(field, inValue)
    }

    fun leSql(column: SFunction<T, *>, inValue: String): C {
        return leSql(true, column, inValue)
    }

    fun notInSql(column: SFunction<T, *>, inValue: String): C {
        return notInSql(true, column, inValue)
    }

    fun notInSql(condition: Boolean, column: SFunction<T, *>, inValue: String): C {
        if (!condition) {
            return c
        }
        val field: String = field(column)
        return notInSql(field, inValue)
    }

    fun groupBy(condition: Boolean, column: SFunction<T, *>): C {
        if (!condition) {
            return c
        }
        val field: String = field(column)
        return groupBy(field)
    }

    fun groupBy(column: SFunction<T, *>): C {
        return groupBy(true, column)
    }

    fun groupByLambda(condition: Boolean, columns: Collection<SFunction<T, *>>): C {
        if (!condition) {
            return c
        }
        return groupBy(fields(columns))
    }

    fun groupByLambda(columns: Collection<SFunction<T, *>>): C {
        return groupByLambda(true, columns)
    }

    @SafeVarargs
    fun groupBy(column: SFunction<T, *>, vararg columns: SFunction<T, *>): C {
        return groupBy(true, column, *columns)
    }

    @SafeVarargs
    fun groupBy(condition: Boolean, column: SFunction<T, *>, vararg columns: SFunction<T, *>): C {
        if (!condition) {
            return c
        }
        val fields = fields(columns.toList())
        fields.add(field(column))
        return groupBy(fields)
    }

    fun orderByAsc(condition: Boolean, column: SFunction<T, *>): C {
        return orderBy(condition, true, column)
    }

    fun orderByAsc(column: SFunction<T, *>): C {
        return orderByAsc(true, column)
    }

    fun orderByAscLambda(condition: Boolean, columns: Collection<SFunction<T, *>>): C {
        return orderByLambda(condition, true, columns)
    }

    fun orderByAscLambda(columns: Collection<SFunction<T, *>>): C {
        return orderByAscLambda(true, columns)
    }

    @SafeVarargs
    fun orderByAsc(column: SFunction<T, *>, vararg columns: SFunction<T, *>): C {
        return orderByAsc(true, column, *columns)
    }

    @SafeVarargs
    fun orderByAsc(condition: Boolean, column: SFunction<T, *>, vararg columns: SFunction<T, *>): C {
        return orderBy(condition, true, column, *columns)
    }

    fun orderByDesc(condition: Boolean, column: SFunction<T, *>): C {
        return orderBy(condition, false, column)
    }

    fun orderByDesc(column: SFunction<T, *>): C {
        return orderByDesc(true, column)
    }

    fun orderByDescLambda(condition: Boolean, columns: Collection<SFunction<T, *>>): C {
        return orderByLambda(condition, false, columns)
    }

    fun orderByDescLambda(columns: Collection<SFunction<T, *>>): C {
        return orderByDescLambda(true, columns)
    }

    @SafeVarargs
    fun orderByDesc(column: SFunction<T, *>, vararg columns: SFunction<T, *>): C {
        return orderByDesc(true, column, *columns)
    }

    @SafeVarargs
    fun orderByDesc(condition: Boolean, column: SFunction<T, *>, vararg columns: SFunction<T, *>): C {
        return orderBy(condition, false, column, *columns)
    }

    fun orderBy(condition: Boolean, isAsc: Boolean, column: SFunction<T, *>): C {
        if (!condition) {
            return c
        }
        val field: String = field(column)
        return orderBy(true, isAsc, field)
    }

    fun orderByLambda(condition: Boolean, isAsc: Boolean, columns: Collection<SFunction<T, *>>): C {
        if (!condition) {
            return c
        }
        return orderBy(true, isAsc, fields(columns))
    }

    @SafeVarargs
    fun orderBy(condition: Boolean, isAsc: Boolean, column: SFunction<T, *>, vararg columns: SFunction<T, *>): C {
        if (!condition) {
            return c
        }
        val fields = fields(columns.toList())
        fields.add(field(column))
        return orderBy(true, isAsc, fields)
    }

    // endregion

    // region func ifPresent
    fun <V : Any> inIfPresent(column: SFunction<T, V?>, coll: Collection<V>?): C {
        return `in`(isPresent(coll), column, coll ?: emptyList())
    }

    fun <V : Any> inIfPresent(column: SFunction<T, V?>, vararg values: V): C {
        return `in`(isPresent(values), column, *values)
    }

    fun <V : Any> notInIfPresent(column: SFunction<T, V?>, coll: Collection<V>?): C {
        return notIn(isPresent(coll), column, coll ?: emptyList())
    }

    fun <V : Any> notInIfPresent(column: SFunction<T, V?>, vararg value: V): C {
        return notIn(isPresent(value), column, *value)
    }

    // endregion

    // region func extended

    fun <E : Any> `in`(column: SFunction<T, *>, consumer: Consumer<QueryWrapper<E>>): C {
        return `in`(true, column, consumer)
    }

    fun <E : Any> `in`(condition: Boolean, column: SFunction<T, *>, consumer: Consumer<QueryWrapper<E>>): C {
        if (!condition) {
            return c
        }
        val filed: String = field(column)
        return `in`(true, filed, consumer)
    }

    fun <E : Any> notIn(column: SFunction<T, *>, consumer: Consumer<QueryWrapper<E>>): C {
        return notIn(true, column, consumer)
    }

    fun <E : Any> notIn(condition: Boolean, column: SFunction<T, *>, consumer: Consumer<QueryWrapper<E>>): C {
        if (!condition) {
            return c
        }
        val filed: String = field(column)
        return notIn(true, filed, consumer)
    }

    // endregion
}
