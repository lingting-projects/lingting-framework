package live.lingting.framework.mybatis.datascope

import live.lingting.framework.datascope.DataScope
import net.sf.jsqlparser.expression.Alias
import net.sf.jsqlparser.expression.Expression

interface JSqlDataScope : DataScope<String, JSqlDataScopeParams, Expression>

data class JSqlDataScopeParams(
    /**
     * 表名
     */
    val name: String,
    /**
     * 别名
     */
    val alias: Alias,
)
