package live.lingting.framework.mybatis.wrapper

import com.baomidou.mybatisplus.core.conditions.AbstractLambdaWrapper
import com.baomidou.mybatisplus.core.conditions.ISqlSegment
import com.baomidou.mybatisplus.core.conditions.SharedString
import com.baomidou.mybatisplus.core.conditions.query.Query
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper
import com.baomidou.mybatisplus.core.toolkit.ArrayUtils
import com.baomidou.mybatisplus.core.toolkit.Assert
import com.baomidou.mybatisplus.core.toolkit.support.SFunction
import net.sf.jsqlparser.expression.Expression
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.Predicate

/**
 * 增加了一些简单条件的 IfPresent 条件 支持，Collection String Object 等等判断是否为空，或者是否为null
 *
 * @author Hccake 2021/1/14
 * @version 1.0
 */
open class LambdaQueryWrapperX<T> : AbstractLambdaWrapper<T, LambdaQueryWrapperX<T?>>, Query<LambdaQueryWrapperX<T>, T?, SFunction<T, *>> {
    /**
     * 查询字段
     */
    private var sqlSelect: SharedString? = SharedString()

    /**
     * 不建议直接 new 该实例，使用 WrappersX.lambdaQueryX(entity)
     */
    /**
     * 不建议直接 new 该实例，使用 WrappersX.lambdaQueryX(entity)
     */
    @JvmOverloads
    constructor(entity: T? = null as T?) {
        super.setEntity(entity)
        super.initNeed()
    }

    /**
     * 不建议直接 new 该实例，使用 Wrappers.lambdaQuery(entity)
     */
    constructor(entityClass: Class<T>?) {
        super.setEntityClass(entityClass)
        super.initNeed()
    }

    /**
     * 不建议直接 new 该实例，使用 Wrappers.lambdaQuery(...)
     */
    internal constructor(
        entity: T, entityClass: Class<T>?, sqlSelect: SharedString?, paramNameSeq: AtomicInteger?,
        paramNameValuePairs: Map<String?, Any?>?, mergeSegments: MergeSegments?, lastSql: SharedString?,
        sqlComment: SharedString?, sqlFirst: SharedString?
    ) {
        super.setEntity(entity)
        super.setEntityClass(entityClass)
        this.paramNameSeq = paramNameSeq
        this.paramNameValuePairs = paramNameValuePairs
        this.expression = mergeSegments
        this.sqlSelect = sqlSelect
        this.lastSql = lastSql
        this.sqlComment = sqlComment
        this.sqlFirst = sqlFirst
    }

    /**
     * SELECT 部分 SQL 设置
     * @param columns 查询字段
     */
    @SafeVarargs
    override fun select(vararg columns: SFunction<T, *>): LambdaQueryWrapperX<T> {
        if (ArrayUtils.isNotEmpty(columns)) {
            sqlSelect!!.setStringValue(columnsToString(false, *columns))
        }
        return typedThis
    }

    override fun select(condition: Boolean, columns: List<SFunction<T, *>>): LambdaQueryWrapperX<T> {
        if (condition) {
            sqlSelect!!.setStringValue(columnsToString(false, columns))
        }
        return typedThis
    }

    /**
     * 过滤查询的字段信息(主键除外!)
     *
     *
     * 例1: 只要 java 字段名以 "test" 开头的 -> select(i -&gt; i.getProperty().startsWith("test"))
     *
     *
     *
     * 例2: 只要 java 字段属性是 CharSequence 类型的 -> select(TableFieldInfo::isCharSequence)
     *
     *
     *
     * 例3: 只要 java 字段没有填充策略的 -> select(i -&gt; i.getFieldFill() == FieldFill.DEFAULT)
     *
     *
     *
     * 例4: 要全部字段 -> select(i -&gt; true)
     *
     *
     *
     * 例5: 只要主键字段 -> select(i -&gt; false)
     *
     * @param predicate 过滤方式
     * @return this
     */
    override fun select(entityClass: Class<T?>?, predicate: Predicate<TableFieldInfo>): LambdaQueryWrapperX<T> {
        var entityClass = entityClass
        if (entityClass == null) {
            entityClass = getEntityClass()
        } else {
            setEntityClass(entityClass)
        }
        Assert.notNull(entityClass, "entityClass can not be null")
        sqlSelect!!.setStringValue(TableInfoHelper.getTableInfo(entityClass).chooseSelect(predicate))
        return typedThis
    }

    override fun getSqlSelect(): String {
        return sqlSelect!!.stringValue
    }

    /**
     * 用于生成嵌套 sql
     *
     *
     * 故 sqlSelect 不向下传递
     *
     */
    override fun instance(): LambdaQueryWrapperX<T?> {
        return LambdaQueryWrapperX(
            entity, getEntityClass(), null, paramNameSeq, paramNameValuePairs,
            MergeSegments(), SharedString.emptyString(), SharedString.emptyString(),
            SharedString.emptyString()
        )
    }

    override fun clear() {
        super.clear()
        sqlSelect!!.toNull()
    }

    // ======= 分界线，以上 copy 自 mybatis-plus 源码 =====
    fun eqIfPresent(column: SFunction<T, *>?, `val`: Any?): LambdaQueryWrapperX<T> {
        return super.eq(isPresent(`val`), column, `val`)
    }

    fun neIfPresent(column: SFunction<T, *>?, `val`: Any?): LambdaQueryWrapperX<T> {
        return super.ne(isPresent(`val`), column, `val`)
    }

    fun gtIfPresent(column: SFunction<T, *>?, `val`: Any?): LambdaQueryWrapperX<T> {
        return super.gt(isPresent(`val`), column, `val`)
    }

    fun geIfPresent(column: SFunction<T, *>?, `val`: Any?): LambdaQueryWrapperX<T> {
        return super.ge(isPresent(`val`), column, `val`)
    }

    fun ltIfPresent(column: SFunction<T, *>?, `val`: Any?): LambdaQueryWrapperX<T> {
        return super.lt(isPresent(`val`), column, `val`)
    }

    fun leIfPresent(column: SFunction<T, *>?, `val`: Any?): LambdaQueryWrapperX<T> {
        return super.le(isPresent(`val`), column, `val`)
    }

    fun likeIfPresent(column: SFunction<T, *>?, `val`: Any?): LambdaQueryWrapperX<T> {
        return super.like(isPresent(`val`), column, `val`)
    }

    fun notLikeIfPresent(column: SFunction<T, *>?, `val`: Any?): LambdaQueryWrapperX<T> {
        return super.notLike(isPresent(`val`), column, `val`)
    }

    fun likeLeftIfPresent(column: SFunction<T, *>?, `val`: Any?): LambdaQueryWrapperX<T> {
        return super.likeLeft(isPresent(`val`), column, `val`)
    }

    fun likeRightIfPresent(column: SFunction<T, *>?, `val`: Any?): LambdaQueryWrapperX<T> {
        return super.likeRight(isPresent(`val`), column, `val`)
    }

    fun inIfPresent(column: SFunction<T, *>?, vararg values: Any?): LambdaQueryWrapperX<T> {
        return super.`in`(isPresent(values), column, values)
    }

    fun inIfPresent(column: SFunction<T, *>?, values: Collection<*>?): LambdaQueryWrapperX<T> {
        return super.`in`(isPresent(values), column, values)
    }

    fun notInIfPresent(column: SFunction<T, *>?, vararg values: Any?): LambdaQueryWrapperX<T> {
        return super.notIn(isPresent(values), column, values)
    }

    fun notInIfPresent(column: SFunction<T, *>?, values: Collection<*>?): LambdaQueryWrapperX<T> {
        return super.notIn(isPresent(values), column, values)
    }

    // region customize
    fun appendSqlSegment(segment: ISqlSegment?): LambdaQueryWrapperX<T> {
        this.appendSqlSegments(ISqlSegment { "" }, segment)
        return this
    }

    fun addSql(sql: String?): LambdaQueryWrapperX<T> {
        return this.appendSqlSegment { sql }
    }

    fun addExpression(expression: Expression): LambdaQueryWrapperX<T> {
        return this.appendSqlSegment { String.format("(%s)", expression.toString()) }
    } // endregion
}
