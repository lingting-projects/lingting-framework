package live.lingting.framework.mybatis.wrapper

import com.baomidou.mybatisplus.core.conditions.ISqlSegment
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments
import com.baomidou.mybatisplus.core.enums.SqlKeyword
import com.baomidou.mybatisplus.core.enums.WrapperKeyword
import com.baomidou.mybatisplus.core.toolkit.StringUtils
import java.lang.StringBuilder
import java.util.ArrayList
import java.util.HashMap
import java.util.HashSet
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.Consumer
import java.util.function.Supplier
import java.util.regex.Pattern
import live.lingting.framework.util.CollectionUtils
import live.lingting.framework.util.ValueUtils

@Suppress("UNCHECKED_CAST")
abstract class AbstractWrapper<T, C : AbstractWrapper<T, C>> : com.baomidou.mybatisplus.core.conditions.Wrapper<T>(), com.baomidou.mybatisplus.core.conditions.interfaces.Compare<C, String>, com.baomidou.mybatisplus.core.conditions.interfaces.Nested<C, C>, com.baomidou.mybatisplus.core.conditions.interfaces.Join<C>, com.baomidou.mybatisplus.core.conditions.interfaces.Func<C, String>, ISqlSegment {

    protected val c = this as C

    protected val segments = MergeSegments()

    /**
     * 查询的字段
     */
    protected val fields = HashSet<String>()

    /**
     * sql 参数
     */
    protected val params = HashMap<String, Any?>()

    // region common
    /**
     * 表的别名.
     */
    protected var alias: String? = null

    fun alias(value: String): C {
        this.alias = value
        return c
    }

    protected var entityClass: Class<T>? = null

    fun cls(cls: Class<T>?): C {
        this.entityClass = cls
        return c
    }

    abstract fun instance(): C

    protected fun convertField(value: String): String {
        // 未设置别名, 或传入字段已设置别名. 则直接使用
        if (StringUtils.isBlank(alias) || value.contains(".")) {
            return value
        }
        return "$alias.$value"
    }

    fun isPresent(o: Any): Boolean {
        return ValueUtils.isPresent(o)
    }

    abstract fun getSafeSql(): String

    // endregion

    // region append
    fun appendSql(sql: String): C {
        return appendSql(null, sql)
    }

    @JvmOverloads
    fun appendSql(keyword: SqlKeyword?, sql: String? = null): C {
        return appendSql(true, keyword, sql)
    }

    fun appendSql(field: String?, keyword: SqlKeyword?, sql: String?): C {
        return appendSql(true, field, keyword, sql)
    }

    fun appendSql(condition: Boolean, keyword: SqlKeyword?, sql: String?): C {
        return appendSql(condition, null, keyword, sql)
    }

    fun appendSql(condition: Boolean, field: String?, keyword: SqlKeyword?, sql: String?): C {
        if (!condition) {
            return c
        }

        val list: MutableList<ISqlSegment> = ArrayList<ISqlSegment>()

        if (StringUtils.isNotBlank(field)) {
            val name = convertField(field!!)
            list.add(ISqlSegment { name })
        }

        list.add(if (keyword == null) WrapperKeyword.APPLY else keyword)

        if (StringUtils.isNotBlank(sql)) {
            list.add(ISqlSegment { sql })
        }

        segments.add(*list.toTypedArray<ISqlSegment>())
        return c
    }

    fun <E> appendSql(condition: Boolean, field: String, keyword: SqlKeyword, consumer: Consumer<QueryWrapper<E>>): C {
        if (!condition) {
            return c
        }
        val query = Wrappers.query<E>()
        query.paramId = paramId
        consumer.accept(query)
        params.putAll(query.params)
        return appendSql(field, keyword, "(" + query.getSafeSql() + ")")
    }

    fun appendCondition(condition: Boolean, field: String?, keyword: SqlKeyword): C {
        return appendCondition(condition, field, keyword, null)
    }

    fun appendCondition(condition: Boolean, field: String?, keyword: SqlKeyword, arg: Any?): C {
        if (!condition) {
            return c
        }

        val builder = StringBuilder()
        val supplier = if (arg == null) {
            null
        } else if (arg is Supplier<*>) {
            arg
        } else {
            Supplier { arg }
        }

        if (supplier != null) {
            val arg = supplier.get()
            val isMulti: Boolean = CollectionUtils.isMulti(arg)
            if (isMulti) {
                builder.append("(")
            }
            for (o in CollectionUtils.multiToList(arg)) {
                val param = safeParam(o!!)
                builder.append(param).append(",")
            }
            if (builder.isNotEmpty()) {
                val index = builder.length - 1
                builder.deleteCharAt(index)
            }
            if (isMulti) {
                builder.append(")")
            }
        }

        return appendSql(field, keyword, builder.toString())
    }

    fun appendCondition(
        condition: Boolean, field: String, keyword: SqlKeyword, valKeyword: SqlKeyword,
        vararg args: Any
    ): C {
        if (!condition) {
            return c
        }

        val delimiter = " " + valKeyword.sqlSegment + " "
        val sql = args.joinToString(delimiter) { this.safeParam(it) }
        return appendSql(true, field, keyword, sql)
    }

    fun appendCondition(condition: Boolean, keyword: SqlKeyword?, consumer: Consumer<C>): C {
        if (!condition) {
            return c
        }

        val c = instance()
        consumer.accept(c)
        return appendCondition<C>(true, keyword, c)
    }

    fun <O : AbstractWrapper<*, *>> appendCondition(condition: Boolean, keyword: SqlKeyword?, o: O): C {
        if (!condition) {
            return c
        }
        params.putAll(o.params)
        appendSql(keyword)
        segments.add(o.segments.normal, o.segments.groupBy, o.segments.having, o.segments.orderBy)
        return c
    }

    fun appendCondition(condition: Boolean, keyword: SqlKeyword?, sql: String, vararg args: Any): C {
        var sql = sql
        if (!condition || StringUtils.isBlank(sql)) {
            return c
        }

        for (i in args.indices) {
            val r = "{$i}"
            val arg: Any = args[i]
            val param = safeParam(arg)
            sql = sql.replace(r, param)
        }

        return appendSql(keyword, sql)
    }

    // endregion

    // region 兼容wrapper
    protected var paramId: AtomicInteger = AtomicInteger()

    /**
     * @param prefix # 或者 $
     */
    protected fun wrapperParam(prefix: String, mapping: String?, value: Any?): String {
        val id = "PARAM" + paramId.incrementAndGet()
        val field = Wrappers.PARAM + ".params." + id
        params.put(id, value)
        val builder = StringBuilder(prefix).append("{").append(field)
        if (StringUtils.isNotBlank(mapping)) {
            builder.append(",").append(mapping)
        }
        return builder.append("}").toString()
    }

    fun safeParam(value: Any?): String {
        return safeParam(null, value)
    }

    fun safeParam(mapping: String?, value: Any?): String {
        return wrapperParam("#", mapping, value)
    }

    fun unsafeParam(value: Any?): String {
        return unsafeParam(null, value)
    }

    fun unsafeParam(mapping: String?, value: Any?): String {
        return wrapperParam("$", mapping, value)
    }

    fun getParamAlias(): String {
        return Wrappers.PARAM
    }

    fun getParamNameValuePairs(): Map<String, Any?> {
        return params
    }

    override fun getSqlSelect(): String? {
        if (CollectionUtils.isEmpty(fields)) {
            return null
        }
        return fields.joinToString(", ")
    }

    override fun getSqlComment(): String? {
        return null
    }

    override fun getSqlSegment(): String {
        val segment: String = segments.sqlSegment
        if (StringUtils.isNotBlank(last)) {
            return "$segment $last"
        }
        return segment
    }

    override fun getCustomSqlSegment(): String {
        val segment = getSqlSegment()
        if (StringUtils.isBlank(segment)) {
            return ""
        }
        return " WHERE $segment"
    }

    /**
     * 获取格式化后的执行sql
     * @return sql
     * @since 3.3.1
     */
    override fun getTargetSql(): String {
        return getSqlSegment().replace("#\\{.+}".toRegex(), "")
    }

    override fun getExpression(): MergeSegments {
        return segments
    }

    var e: T? = null

    override fun getEntity(): T? {
        return e
    }

    override fun clear() {
        alias = ""
        segments.clear()
        fields.clear()
        params.clear()
        first = ""
        last = ""
    }

    fun formatSqlByParam(sql: String?, vararg params: Any?): String? {
        if (StringUtils.isBlank(sql)) {
            return null
        }

        if (params.isEmpty()) {
            return sql
        }

        var string = sql!!

        params.forEachIndexed { i, p ->
            val flag = "{$i}"
            if (string.contains(flag)) {
                val param = safeParam(p)
                string = string.replace(flag, param)
            } else {
                val pattern = Pattern.compile("[{]$i,[a-zA-Z0-9.,=]+")
                val matcher = pattern.matcher(string)
                if (!matcher.find()) {
                    throw IllegalArgumentException("Sql param syntax error! not found: $flag")
                }
                while (true) {
                    val group = matcher.group()
                    if (StringUtils.isNotBlank(group)) {
                        val mapping = group.substring(flag.length, group.length - 1)
                        val param = safeParam(mapping, p)
                        string = string.replace(group, param)
                    }

                    if (!matcher.find()) {
                        break
                    }
                }
            }
        }

        return string
    }

    // endregion

    // region compare
    override fun <V> allEq(condition: Boolean, params: MutableMap<String, V>, null2IsNull: Boolean): C {
        if (condition && !CollectionUtils.isEmpty(params)) {
            params.forEach { (k: String, v: V) ->
                if (StringUtils.checkValNotNull(v)) {
                    eq(k, v)
                } else {
                    if (null2IsNull) {
                        isNull(k)
                    }
                }
            }
        }
        return c
    }

    override fun <V> allEq(condition: Boolean, filter: java.util.function.BiPredicate<String, V>, params: MutableMap<String, V>, null2IsNull: Boolean): C {
        if (condition && !CollectionUtils.isEmpty(params)) {
            params.forEach { (k: String, v: V) ->
                if (filter.test(k, v)) {
                    if (StringUtils.checkValNotNull(v)) {
                        eq(k, v)
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

    override fun eq(condition: Boolean, column: String, `val`: Any): C {
        return appendCondition(condition, column, SqlKeyword.EQ, `val`)
    }

    override fun ne(condition: Boolean, column: String, `val`: Any): C {
        return appendCondition(condition, column, SqlKeyword.NE, `val`)
    }

    override fun gt(condition: Boolean, column: String, `val`: Any): C {
        return appendCondition(condition, column, SqlKeyword.GT, `val`)
    }

    override fun ge(condition: Boolean, column: String, `val`: Any): C {
        return appendCondition(condition, column, SqlKeyword.GE, `val`)
    }

    override fun lt(condition: Boolean, column: String, `val`: Any): C {
        return appendCondition(condition, column, SqlKeyword.LT, `val`)
    }

    override fun le(condition: Boolean, column: String, `val`: Any): C {
        return appendCondition(condition, column, SqlKeyword.LE, `val`)
    }

    override fun between(condition: Boolean, column: String, val1: Any, val2: Any): C {
        return appendCondition(condition, column, SqlKeyword.BETWEEN, SqlKeyword.AND, val1, val2)
    }

    override fun notBetween(condition: Boolean, column: String, val1: Any, val2: Any): C {
        return appendCondition(condition, column, SqlKeyword.NOT_BETWEEN, SqlKeyword.AND, val1, val2)
    }

    override fun like(condition: Boolean, column: String, `val`: Any): C {
        return appendCondition(condition, column, SqlKeyword.LIKE, Supplier { "%$`val`%" })
    }

    override fun notLike(condition: Boolean, column: String, `val`: Any): C {
        return appendCondition(condition, column, SqlKeyword.NOT_LIKE, Supplier { "%$`val`%" })
    }

    override fun notLikeLeft(condition: Boolean, column: String, `val`: Any): C {
        return appendCondition(condition, column, SqlKeyword.LIKE, Supplier { "%$`val`" })
    }

    override fun notLikeRight(condition: Boolean, column: String, `val`: Any): C {
        return appendCondition(condition, column, SqlKeyword.LIKE, Supplier { "$`val`%" })
    }

    override fun likeLeft(condition: Boolean, column: String, `val`: Any): C {
        return appendCondition(condition, column, SqlKeyword.NOT_LIKE, Supplier { "%$`val`" })
    }

    override fun likeRight(condition: Boolean, column: String, `val`: Any): C {
        return appendCondition(condition, column, SqlKeyword.NOT_LIKE, Supplier { "$`val`%" })
    }

    // endregion

    // region compare ifPresent
    fun eqIfPresent(column: String, `val`: Any): C {
        return eq(isPresent(`val`), column, `val`)
    }

    fun neIfPresent(column: String, `val`: Any): C {
        return ne(isPresent(`val`), column, `val`)
    }

    fun gtIfPresent(column: String, `val`: Any): C {
        return gt(isPresent(`val`), column, `val`)
    }

    fun geIfPresent(column: String, `val`: Any): C {
        return ge(isPresent(`val`), column, `val`)
    }

    fun ltIfPresent(column: String, `val`: Any): C {
        return lt(isPresent(`val`), column, `val`)
    }

    fun leIfPresent(column: String, `val`: Any): C {
        return le(isPresent(`val`), column, `val`)
    }

    fun betweenIfPresent(column: String, val1: Any, val2: Any): C {
        return between(isPresent(val1) && isPresent(val2), column, val1, val2)
    }

    fun notBetweenIfPresent(column: String, val1: Any, val2: Any): C {
        return notBetween(isPresent(val1) && isPresent(val2), column, val1, val2)
    }

    fun likeIfPresent(column: String, `val`: Any): C {
        return like(isPresent(`val`), column, `val`)
    }

    fun notLikeIfPresent(column: String, `val`: Any): C {
        return notLike(isPresent(`val`), column, `val`)
    }

    fun notLikeLeftIfPresent(column: String, `val`: Any): C {
        return notLikeLeft(isPresent(`val`), column, `val`)
    }

    fun notLikeRightIfPresent(column: String, `val`: Any): C {
        return notLikeRight(isPresent(`val`), column, `val`)
    }

    fun likeLeftIfPresent(column: String, `val`: Any): C {
        return likeLeft(isPresent(`val`), column, `val`)
    }

    fun likeRightIfPresent(column: String, `val`: Any): C {
        return likeRight(isPresent(`val`), column, `val`)
    }

    // endregion

    // region compare extended
    // endregion

    // region nested
    override fun and(condition: Boolean, consumer: Consumer<C>): C {
        return appendCondition(condition, SqlKeyword.AND, consumer)
    }

    override fun or(condition: Boolean, consumer: Consumer<C>): C {
        return appendCondition(condition, SqlKeyword.OR, consumer)
    }

    override fun nested(condition: Boolean, consumer: Consumer<C>): C {
        return appendCondition(condition, null, consumer)
    }

    override fun not(condition: Boolean, consumer: Consumer<C>): C {
        return appendCondition(condition, SqlKeyword.NOT, consumer)
    }

    // endregion

    // region join
    override fun or(condition: Boolean): C {
        return appendSql(SqlKeyword.OR)
    }

    override fun apply(condition: Boolean, applySql: String, vararg values: Any): C {
        return appendCondition(condition, null, applySql, *values)
    }

    var last: String? = null
        protected set

    override fun last(condition: Boolean, lastSql: String): C {
        if (condition) {
            last = lastSql
        }
        return c
    }

    fun limit(count: Number): C {
        return last(" limit " + count.toLong())
    }

    fun limitOne(): C {
        return limit(1)
    }

    override fun comment(condition: Boolean, comment: String): C {
        return c
    }

    var first: String? = null
        protected set

    override fun getSqlFirst(): String? {
        return first
    }

    override fun first(condition: Boolean, firstSql: String): C {
        if (condition) {
            first = firstSql
        }
        return c
    }

    override fun exists(condition: Boolean, existsSql: String, vararg values: Any): C {
        return appendCondition(condition, SqlKeyword.EXISTS, existsSql, *values)
    }

    override fun notExists(condition: Boolean, existsSql: String, vararg values: Any): C {
        return appendCondition(condition, SqlKeyword.NOT_EXISTS, existsSql, *values)
    }

    // endregion

    // region func
    override fun isNull(condition: Boolean, column: String): C {
        return appendCondition(condition, column, SqlKeyword.IS_NULL)
    }

    override fun isNotNull(condition: Boolean, column: String): C {
        return appendCondition(condition, column, SqlKeyword.IS_NOT_NULL)
    }

    override fun `in`(condition: Boolean, column: String, coll: MutableCollection<*>): C {
        return appendCondition(condition, column, SqlKeyword.IN, coll)
    }

    override fun `in`(condition: Boolean, column: String, vararg values: Any): C {
        return appendCondition(condition, column, SqlKeyword.IN, values)
    }

    override fun notIn(condition: Boolean, column: String, coll: MutableCollection<*>): C {
        return appendCondition(condition, column, SqlKeyword.NOT_IN, coll)
    }

    override fun notIn(condition: Boolean, column: String, vararg values: Any): C {
        return appendCondition(condition, column, SqlKeyword.NOT_IN, values)
    }

    override fun inSql(condition: Boolean, column: String, inValue: String): C {
        return appendSql(condition, column, SqlKeyword.IN, inValue)
    }

    override fun gtSql(condition: Boolean, column: String, inValue: String): C {
        return appendSql(condition, column, SqlKeyword.GT, inValue)
    }

    override fun geSql(condition: Boolean, column: String, inValue: String): C {
        return appendSql(condition, column, SqlKeyword.GE, inValue)
    }

    override fun ltSql(condition: Boolean, column: String, inValue: String): C {
        return appendSql(condition, column, SqlKeyword.LT, inValue)
    }

    override fun leSql(condition: Boolean, column: String, inValue: String): C {
        return appendSql(condition, column, SqlKeyword.LE, inValue)
    }

    override fun notInSql(condition: Boolean, column: String, inValue: String): C {
        return appendSql(condition, column, SqlKeyword.NOT_IN, inValue)
    }

    override fun eqSql(condition: Boolean, column: String?, inValue: String): C {
        return appendSql(condition, column, SqlKeyword.EQ, inValue)
    }

    override fun groupBy(condition: Boolean, column: String): C {
        return groupBy(condition, mutableListOf<String>(column))
    }

    override fun groupBy(condition: Boolean, columns: MutableList<String>): C {
        if (!condition) {
            return c
        }
        val collect: String = columns.map { value: String -> this.convertField(value) }.joinToString(", ")
        return appendSql(SqlKeyword.GROUP_BY, collect)
    }

    override fun groupBy(condition: Boolean, column: String, vararg columns: String): C {
        return groupBy(condition, column, columns.toList())
    }

    override fun groupBy(condition: Boolean, column: String, columns: List<String>): C {
        if (!condition) {
            return c
        }
        val list = ArrayList<String>()
        list.add(column)
        list.addAll(columns)
        return groupBy(true, list)
    }

    override fun orderBy(condition: Boolean, isAsc: Boolean, column: String): C {
        return orderBy(condition, isAsc, mutableListOf<String>(column))
    }

    override fun orderBy(condition: Boolean, isAsc: Boolean, columns: MutableList<String>): C {
        if (!condition) {
            return c
        }
        val order = if (isAsc) " ASC" else " DESC"
        val sql: String = columns.map { c -> convertField(c) + order }.joinToString(", ")
        return appendSql(SqlKeyword.ORDER_BY, sql)
    }

    override fun orderBy(condition: Boolean, isAsc: Boolean, column: String, vararg columns: String): C {
        return orderBy(condition, isAsc, column, columns.toList())
    }

    override fun orderBy(condition: Boolean, isAsc: Boolean, column: String, columns: List<String>): C {
        if (!condition) {
            return c
        }
        val list: MutableList<String> = ArrayList<String>()
        list.add(column)
        list.addAll(columns)
        return orderBy(true, isAsc, list)
    }

    override fun having(condition: Boolean, sqlHaving: String, vararg params: Any): C {
        return appendCondition(condition, SqlKeyword.HAVING, sqlHaving, *params)
    }

    override fun func(condition: Boolean, consumer: Consumer<C>): C {
        if (!condition) {
            return c
        }
        consumer.accept(c)
        return c
    }

    // endregion

    // region func ifPresent
    fun inIfPresent(column: String, coll: MutableCollection<*>): C {
        return `in`(isPresent(coll), column, coll)
    }

    fun inIfPresent(column: String, vararg values: Any): C {
        return `in`(isPresent(values), column, *values)
    }

    fun notInIfPresent(column: String, coll: MutableCollection<*>): C {
        return notIn(isPresent(coll), column, coll)
    }

    fun notInIfPresent(column: String, vararg value: Any): C {
        return notIn(isPresent(value), column, *value)
    }

    // endregion

    // region func extended
    fun <E> `in`(field: String, consumer: Consumer<QueryWrapper<E>>): C {
        return `in`<E>(true, field, consumer)
    }

    fun <E> `in`(condition: Boolean, field: String, consumer: Consumer<QueryWrapper<E>>): C {
        return appendSql<E>(condition, field, SqlKeyword.IN, consumer)
    } // endregion
}
