package live.lingting.framework.mybatis.wrapper

import com.baomidou.mybatisplus.core.toolkit.StringUtils.checkValNotNull
import com.baomidou.mybatisplus.core.toolkit.support.SFunction
import java.util.ArrayList
import java.util.function.BiPredicate

abstract class LambdaWrapper<T : Any, C : LambdaWrapper<T, C>> : AbstractWrapper<T, C>() {

    protected fun convertField(sf: SFunction<T, *>): String {
        val column = Wrappers.column(sf)
        val name = column!!.columnSelect
        return convertField(name)
    }

    protected fun convertFields(collection: Collection<SFunction<T, *>?>): MutableList<String> {
        return collection.filterNotNull().map(this::convertField).toMutableList()
    }

    protected fun convertFields(array: Array<SFunction<T, *>?>): MutableList<String> {
        if (array.isEmpty()) {
            return ArrayList<String>()
        }
        return convertFields(array.toList())
    }

    // region compare
    fun <V> allEqLambda(params: Map<SFunction<T, *>, V>): C {
        return allEqLambda<V>(params, true)
    }

    fun <V> allEqLambda(params: Map<SFunction<T, *>, V>, null2IsNull: Boolean): C {
        return allEqLambda<V>(true, params, null2IsNull)
    }

    fun <V> allEqLambda(condition: Boolean, params: Map<SFunction<T, *>, V>, null2IsNull: Boolean): C {
        if (condition && !params.isNullOrEmpty()) {
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

    fun <V> allEqLambda(filter: BiPredicate<SFunction<T, *>, V>, params: Map<SFunction<T, *>, V>): C {
        return allEqLambda<V>(filter, params, true)
    }

    fun <V> allEqLambda(
        filter: BiPredicate<SFunction<T, *>, V>, params: Map<SFunction<T, *>, V>,
        null2IsNull: Boolean
    ): C {
        return allEqLambda<V>(true, filter, params, null2IsNull)
    }

    fun <V> allEqLambda(
        condition: Boolean, filter: BiPredicate<SFunction<T, *>, V>, params: Map<SFunction<T, *>, V>,
        null2IsNull: Boolean
    ): C {
        if (condition && !params.isNullOrEmpty()) {
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

    fun eq(column: SFunction<T, *>, `val`: Any): C {
        return eq(true, column, `val`)
    }

    fun eq(condition: Boolean, column: SFunction<T, *>, `val`: Any): C {
        if (!condition) {
            return c
        }
        val field: String = convertField(column)
        return eq(field, `val`)
    }

    fun ne(column: SFunction<T, *>, `val`: Any): C {
        return ne(true, column, `val`)
    }

    fun ne(condition: Boolean, column: SFunction<T, *>, `val`: Any): C {
        if (!condition) {
            return c
        }
        val field: String = convertField(column)
        return ne(field, `val`)
    }

    fun gt(column: SFunction<T, *>, `val`: Any): C {
        return gt(true, column, `val`)
    }

    fun gt(condition: Boolean, column: SFunction<T, *>, `val`: Any): C {
        if (!condition) {
            return c
        }
        val field: String = convertField(column)
        return gt(field, `val`)
    }

    fun ge(column: SFunction<T, *>, `val`: Any): C {
        return ge(true, column, `val`)
    }

    fun ge(condition: Boolean, column: SFunction<T, *>, `val`: Any): C {
        if (!condition) {
            return c
        }
        val field: String = convertField(column)
        return ge(field, `val`)
    }

    fun lt(column: SFunction<T, *>, `val`: Any): C {
        return lt(true, column, `val`)
    }

    fun lt(condition: Boolean, column: SFunction<T, *>, `val`: Any): C {
        if (!condition) {
            return c
        }
        val field: String = convertField(column)
        return lt(field, `val`)
    }

    fun le(column: SFunction<T, *>, `val`: Any): C {
        return le(true, column, `val`)
    }

    fun le(condition: Boolean, column: SFunction<T, *>, `val`: Any): C {
        if (!condition) {
            return c
        }
        val field: String = convertField(column)
        return le(field, `val`)
    }

    fun between(column: SFunction<T, *>, val1: Any, val2: Any): C {
        return between(true, column, val1, val2)
    }

    fun between(condition: Boolean, column: SFunction<T, *>, val1: Any, val2: Any): C {
        if (!condition) {
            return c
        }
        val field: String = convertField(column)
        return between(field, val1, val2)
    }

    fun notBetween(column: SFunction<T, *>, val1: Any, val2: Any): C {
        return notBetween(true, column, val1, val2)
    }

    fun notBetween(condition: Boolean, column: SFunction<T, *>, val1: Any, val2: Any): C {
        if (!condition) {
            return c
        }
        val field: String = convertField(column)
        return notBetween(field, val1, val2)
    }

    fun like(column: SFunction<T, *>, `val`: Any): C {
        return like(true, column, `val`)
    }

    fun like(condition: Boolean, column: SFunction<T, *>, `val`: Any): C {
        if (!condition) {
            return c
        }
        val field: String = convertField(column)
        return like(field, `val`)
    }

    fun notLike(column: SFunction<T, *>, `val`: Any): C {
        return notLike(true, column, `val`)
    }

    fun notLike(condition: Boolean, column: SFunction<T, *>, `val`: Any): C {
        if (!condition) {
            return c
        }
        val field: String = convertField(column)
        return notLike(field, `val`)
    }

    fun notLikeLeft(column: SFunction<T, *>, `val`: Any): C {
        return notLikeLeft(true, column, `val`)
    }

    fun notLikeLeft(condition: Boolean, column: SFunction<T, *>, `val`: Any): C {
        if (!condition) {
            return c
        }
        val field: String = convertField(column)
        return notLikeLeft(field, `val`)
    }

    fun notLikeRight(column: SFunction<T, *>, `val`: Any): C {
        return notLikeRight(true, column, `val`)
    }

    fun notLikeRight(condition: Boolean, column: SFunction<T, *>, `val`: Any): C {
        if (!condition) {
            return c
        }
        val field: String = convertField(column)
        return notLikeRight(field, `val`)
    }

    fun likeLeft(column: SFunction<T, *>, `val`: Any): C {
        return likeLeft(true, column, `val`)
    }

    fun likeLeft(condition: Boolean, column: SFunction<T, *>, `val`: Any): C {
        if (!condition) {
            return c
        }
        val field: String = convertField(column)
        return likeLeft(field, `val`)
    }

    fun likeRight(column: SFunction<T, *>, `val`: Any): C {
        return likeRight(true, column, `val`)
    }

    fun likeRight(condition: Boolean, column: SFunction<T, *>, `val`: Any): C {
        if (!condition) {
            return c
        }
        val field: String = convertField(column)
        return likeRight(field, `val`)
    }

    // endregion
    // region compare ifPresent
    fun eqIfPresent(column: SFunction<T, *>, `val`: Any): C {
        return eq(isPresent(`val`), column, `val`)
    }

    fun neIfPresent(column: SFunction<T, *>, `val`: Any): C {
        return ne(isPresent(`val`), column, `val`)
    }

    fun gtIfPresent(column: SFunction<T, *>, `val`: Any): C {
        return gt(isPresent(`val`), column, `val`)
    }

    fun geIfPresent(column: SFunction<T, *>, `val`: Any): C {
        return ge(isPresent(`val`), column, `val`)
    }

    fun ltIfPresent(column: SFunction<T, *>, `val`: Any): C {
        return lt(isPresent(`val`), column, `val`)
    }

    fun leIfPresent(column: SFunction<T, *>, `val`: Any): C {
        return le(isPresent(`val`), column, `val`)
    }

    fun betweenIfPresent(column: SFunction<T, *>, val1: Any, val2: Any): C {
        return between(isPresent(val1) && isPresent(val2), column, val1, val2)
    }

    fun notBetweenIfPresent(column: SFunction<T, *>, val1: Any, val2: Any): C {
        return notBetween(isPresent(val1) && isPresent(val2), column, val1, val2)
    }

    fun likeIfPresent(column: SFunction<T, *>, `val`: Any): C {
        return like(isPresent(`val`), column, `val`)
    }

    fun notLikeIfPresent(column: SFunction<T, *>, `val`: Any): C {
        return notLike(isPresent(`val`), column, `val`)
    }

    fun notLikeLeftIfPresent(column: SFunction<T, *>, `val`: Any): C {
        return notLikeLeft(isPresent(`val`), column, `val`)
    }

    fun notLikeRightIfPresent(column: SFunction<T, *>, `val`: Any): C {
        return notLikeRight(isPresent(`val`), column, `val`)
    }

    fun likeLeftIfPresent(column: SFunction<T, *>, `val`: Any): C {
        return likeLeft(isPresent(`val`), column, `val`)
    }

    fun likeRightIfPresent(column: SFunction<T, *>, `val`: Any): C {
        return likeRight(isPresent(`val`), column, `val`)
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
        val field: String = convertField(column)
        return isNull(field)
    }

    fun isNotNull(column: SFunction<T, *>): C {
        return isNotNull(true, column)
    }

    fun isNotNull(condition: Boolean, column: SFunction<T, *>): C {
        if (!condition) {
            return c
        }
        val field: String = convertField(column)
        return isNotNull(field)
    }

    fun `in`(column: SFunction<T, *>, coll: MutableCollection<*>): C {
        return `in`(true, column, coll)
    }

    fun `in`(condition: Boolean, column: SFunction<T, *>, coll: MutableCollection<*>): C {
        if (!condition) {
            return c
        }
        val field: String = convertField(column)
        return `in`(field, coll)
    }

    fun `in`(column: SFunction<T, *>, vararg values: Any): C {
        return `in`(true, column, *values)
    }

    fun `in`(condition: Boolean, column: SFunction<T, *>, vararg values: Any): C {
        if (!condition) {
            return c
        }
        val field: String = convertField(column)
        return `in`(field, *values)
    }

    fun notIn(column: SFunction<T, *>, coll: MutableCollection<*>): C {
        return notIn(true, column, coll)
    }

    fun notIn(condition: Boolean, column: SFunction<T, *>, coll: MutableCollection<*>): C {
        if (!condition) {
            return c
        }
        val field: String = convertField(column)
        return notIn(field, coll)
    }

    fun notIn(column: SFunction<T, *>, vararg value: Any): C {
        return notIn(true, column, *value)
    }

    fun notIn(condition: Boolean, column: SFunction<T, *>, vararg values: Any): C {
        if (!condition) {
            return c
        }
        val field: String = convertField(column)
        return notIn(field, *values)
    }

    fun inSql(column: SFunction<T, *>, inValue: String): C {
        return inSql(true, column, inValue)
    }

    fun inSql(condition: Boolean, column: SFunction<T, *>, inValue: String): C {
        if (!condition) {
            return c
        }
        val field: String = convertField(column)
        return inSql(field, inValue)
    }

    fun gtSql(condition: Boolean, column: SFunction<T, *>, inValue: String): C {
        if (!condition) {
            return c
        }
        val field: String = convertField(column)
        return gtSql(field, inValue)
    }

    fun gtSql(column: SFunction<T, *>, inValue: String): C {
        return gtSql(true, column, inValue)
    }

    fun geSql(condition: Boolean, column: SFunction<T, *>, inValue: String): C {
        if (!condition) {
            return c
        }
        val field: String = convertField(column)
        return geSql(field, inValue)
    }

    fun geSql(column: SFunction<T, *>, inValue: String): C {
        return geSql(true, column, inValue)
    }

    fun ltSql(condition: Boolean, column: SFunction<T, *>, inValue: String): C {
        if (!condition) {
            return c
        }
        val field: String = convertField(column)
        return ltSql(field, inValue)
    }

    fun ltSql(column: SFunction<T, *>, inValue: String): C {
        return ltSql(true, column, inValue)
    }

    fun leSql(condition: Boolean, column: SFunction<T, *>, inValue: String): C {
        if (!condition) {
            return c
        }
        val field: String = convertField(column)
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
        val field: String = convertField(column)
        return notInSql(field, inValue)
    }

    fun groupBy(condition: Boolean, column: SFunction<T, *>): C {
        if (!condition) {
            return c
        }
        val field: String = convertField(column)
        return groupBy(field)
    }

    fun groupBy(column: SFunction<T, *>): C {
        return groupBy(true, column)
    }

    fun groupByLambda(condition: Boolean, columns: MutableList<SFunction<T, *>>): C {
        if (!condition) {
            return c
        }
        return groupBy(convertFields(columns))
    }

    fun groupByLambda(columns: MutableList<SFunction<T, *>>): C {
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
        val fields = convertFields(columns.toList())
        fields.add(convertField(column))
        return groupBy(fields)
    }

    fun orderByAsc(condition: Boolean, column: SFunction<T, *>): C {
        return orderBy(condition, true, column)
    }

    fun orderByAsc(column: SFunction<T, *>): C {
        return orderByAsc(true, column)
    }

    fun orderByAscLambda(condition: Boolean, columns: MutableList<SFunction<T, *>>): C {
        return orderByLambda(condition, true, columns)
    }

    fun orderByAscLambda(columns: MutableList<SFunction<T, *>>): C {
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

    fun orderByDescLambda(condition: Boolean, columns: MutableList<SFunction<T, *>>): C {
        return orderByLambda(condition, false, columns)
    }

    fun orderByDescLambda(columns: MutableList<SFunction<T, *>>): C {
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
        val field: String = convertField(column)
        return orderBy(true, isAsc, field)
    }

    fun orderByLambda(condition: Boolean, isAsc: Boolean, columns: MutableList<SFunction<T, *>>): C {
        if (!condition) {
            return c
        }
        return orderBy(true, isAsc, convertFields(columns))
    }

    @SafeVarargs
    fun orderBy(condition: Boolean, isAsc: Boolean, column: SFunction<T, *>, vararg columns: SFunction<T, *>): C {
        if (!condition) {
            return c
        }
        val fields = convertFields(columns.toList())
        fields.add(convertField(column))
        return orderBy(true, isAsc, fields)
    }

    // endregion
    // region func ifPresent
    fun inIfPresent(column: SFunction<T, *>, coll: MutableCollection<*>): C {
        return `in`(isPresent(coll), column, coll)
    }

    fun inIfPresent(column: SFunction<T, *>, vararg values: Any): C {
        return `in`(isPresent(values), column, *values)
    }

    fun notInIfPresent(column: SFunction<T, *>, coll: MutableCollection<*>): C {
        return notIn(isPresent(coll), column, coll)
    }

    fun notInIfPresent(column: SFunction<T, *>, vararg value: Any): C {
        return notIn(isPresent(value), column, *value)
    }

    // endregion
    // region func extended
    fun <E : Any> `in`(column: SFunction<T, *>, consumer: java.util.function.Consumer<QueryWrapper<E>>): C {
        return `in`<E>(true, column, consumer)
    }

    fun <E : Any> `in`(condition: Boolean, column: SFunction<T, *>, consumer: java.util.function.Consumer<QueryWrapper<E>>): C {
        if (!condition) {
            return c
        }
        val filed: String = convertField(column)
        return `in`<E>(true, filed, consumer)
    } // endregion
}
