package live.lingting.framework.mybatis.datascope

import live.lingting.framework.datascope.DataScope
import net.sf.jsqlparser.expression.Alias
import net.sf.jsqlparser.expression.Expression
import net.sf.jsqlparser.schema.Column

interface JSqlDataScope : DataScope<String, JSqlDataScopeParams, Expression> {

    fun column(field: String, name: String, alias: Alias?): Column {
        return SqlParseUtils.getAliasColumn(name, alias, field)
    }

}

data class JSqlDataScopeParams(
    /**
     * 表名
     */
    val name: String,
    /**
     * 别名
     */
    val alias: Alias?,
)
