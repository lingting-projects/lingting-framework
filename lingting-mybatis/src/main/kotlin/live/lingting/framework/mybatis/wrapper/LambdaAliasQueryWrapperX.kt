package live.lingting.framework.mybatis.wrapper

import com.baomidou.mybatisplus.core.conditions.SharedString
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments
import com.baomidou.mybatisplus.core.toolkit.support.SFunction
import live.lingting.framework.mybatis.alias.TableAlias
import live.lingting.framework.mybatis.alias.TableAliasHelper
import java.util.concurrent.atomic.AtomicInteger

/**
 * 生成可携带表别名的查询条件 当前实体必须被配置表列名注解
 *
 * @author Hccake 2021/1/14
 * @version 1.0
 * @see TableAlias
 */
class LambdaAliasQueryWrapperX<T> : LambdaQueryWrapperX<T?> {
    private val tableAlias: String?

    /**
     * 带别名的查询列 sql 片段，默认为null，第一次使用时加载<br></br>
     * eg. t.id,t.name
     */
    var allAliasSqlSelect: String? = null
        /**
         * 获取查询带别名的查询字段 暂时没有想到好的方法进行查询字段注入 本来的想法是 自定义注入 SqlFragment 但是目前 mybatis-plus 的
         * TableInfo 解析在 xml 解析之后进行，导致 include 标签被提前替换， 先在 wrapper 中做简单处理吧
         * @return String allAliasSqlSelect
         */
        get() {
            if (field == null) {
                field = TableAliasHelper.Companion.tableAliasSelectSql(entityClass)
            }
            return field
        }
        private set

    constructor(entity: T) : super(entity) {
        this.tableAlias = TableAliasHelper.Companion.tableAlias(entityClass)
    }

    constructor(entityClass: Class<T>?) : super(entityClass) {
        this.tableAlias = TableAliasHelper.Companion.tableAlias(getEntityClass())
    }

    /**
     * 不建议直接 new 该实例，使用 Wrappers.lambdaQuery(...)
     */
    internal constructor(
        entity: T, entityClass: Class<T>?, sqlSelect: SharedString?, paramNameSeq: AtomicInteger?,
        paramNameValuePairs: Map<String?, Any?>?, mergeSegments: MergeSegments?, lastSql: SharedString?,
        sqlComment: SharedString?, sqlFirst: SharedString?
    ) : super(
        entity, entityClass, sqlSelect, paramNameSeq, paramNameValuePairs, mergeSegments, lastSql, sqlComment,
        sqlFirst
    ) {
        this.tableAlias = TableAliasHelper.Companion.tableAlias(getEntityClass())
    }

    /**
     * 用于生成嵌套 sql
     *
     *
     * 故 sqlSelect 不向下传递
     *
     */
    override fun instance(): LambdaAliasQueryWrapperX<T?> {
        return LambdaAliasQueryWrapperX(
            entity, entityClass, null, paramNameSeq, paramNameValuePairs,
            MergeSegments(), SharedString.emptyString(), SharedString.emptyString(),
            SharedString.emptyString()
        )
    }

    /**
     * 查询条件构造时添加上表别名
     * @param column 字段Lambda
     * @return 表别名.字段名，如：t.id
     */
    override fun columnToString(column: SFunction<T?, *>?): String {
        if (column is ColumnFunction<*>) {
            val columnFunction = column as ColumnFunction<T?>
            return columnFunction.apply(null)!!
        }
        val columnName = super.columnToString(column, true)
        return if (tableAlias == null) columnName else "$tableAlias.$columnName"
    }
}
