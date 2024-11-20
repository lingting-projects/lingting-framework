package live.lingting.framework.datascope

import net.sf.jsqlparser.expression.Alias
import net.sf.jsqlparser.expression.Expression
import net.sf.jsqlparser.schema.Column

/**
 * @author Hccake 2020/9/28
 * @version 1.0
 */
interface JsqlDataScope {
    /**
     * 数据所对应的资源
     * @return 资源标识
     */
    val resource: String

    /**
     * 判断当前数据权限范围是否需要管理此表
     * @param tableName 当前需要处理的表名
     * @return 如果当前数据权限范围包含当前表名，则返回 true。，否则返回 false
     */
    fun includes(tableName: String): Boolean

    /**
     * 根据表名和表别名，动态生成的 where/or 筛选条件
     * @param tableName 表名
     * @param tableAlias 表别名，可能为空
     * @return 数据规则表达式
     */
    fun getExpression(tableName: String, tableAlias: Alias?): Expression

    fun column(field: String, alias: Alias?): Column {
        return Column(if (alias == null) field else alias.name + "." + field)
    }
}
