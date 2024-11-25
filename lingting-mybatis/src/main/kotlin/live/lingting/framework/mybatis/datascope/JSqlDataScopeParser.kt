package live.lingting.framework.mybatis.datascope

import live.lingting.framework.datascope.exception.DataScopeException
import live.lingting.framework.kt.logger
import net.sf.jsqlparser.parser.CCJSqlParserUtil
import net.sf.jsqlparser.statement.Statement
import net.sf.jsqlparser.statement.Statements
import net.sf.jsqlparser.statement.delete.Delete
import net.sf.jsqlparser.statement.insert.Insert
import net.sf.jsqlparser.statement.select.Select
import net.sf.jsqlparser.statement.update.Update

/**
 * @author lingting 2024/11/25 14:19
 */
abstract class JSqlDataScopeParser(protected val scopes: List<JSqlDataScope>) {

    protected val log = logger()
    protected val matchScopes = mutableSetOf<JSqlDataScope>()

    fun parser(sql: String, isMulti: Boolean): DataScopeParserResult {
        try {
            val statements = if (isMulti) {
                CCJSqlParserUtil.parseStatements(sql)
            } else {
                val statement = CCJSqlParserUtil.parse(sql)
                val statements = Statements()
                statements.add(statement)
                statements
            }

            val sql = statements.mapIndexed { i, s -> parser(s, i, sql) }.joinToString(";")
            return DataScopeParserResult(matchScopes.size, sql)

        } catch (e: Exception) {
            throw DataScopeException("sql parse exception!", e)
        }
    }

    fun parserSingle(sql: String): DataScopeParserResult {
        return parser(sql, false)
    }

    fun parserMulti(sql: String): DataScopeParserResult {
        return parser(sql, true)
    }

    /**
     * 执行 SQL 解析
     * @param statement JsqlParser Statement
     * @return sql
     */
    protected fun parser(statement: Statement, index: Int, sql: String): String {
        var sql = sql
        if (log.isDebugEnabled) {
            log.debug("SQL to parse, SQL: {}", sql)
        }
        if (statement is Insert) {
            this.insert(statement, index, sql)
        } else if (statement is Select) {
            this.select(statement, index, sql)
        } else if (statement is Update) {
            this.update(statement, index, sql)
        } else if (statement is Delete) {
            this.delete(statement, index, sql)
        }
        sql = statement.toString()
        if (log.isDebugEnabled) {
            log.debug("parse the finished SQL: {}", sql)
        }
        return sql
    }

    /**
     * 新增
     */
    protected open fun insert(insert: Insert, index: Int, sql: String) {
        throw UnsupportedOperationException()
    }

    /**
     * 删除
     */
    protected open fun delete(delete: Delete, index: Int, sql: String) {
        throw UnsupportedOperationException()
    }

    /**
     * 更新
     */
    protected open fun update(update: Update, index: Int, sql: String) {
        throw UnsupportedOperationException()
    }

    /**
     * 查询
     */
    protected open fun select(select: Select, index: Int, sql: String) {
        throw UnsupportedOperationException()
    }
}

data class DataScopeParserResult(
    val matchNumber: Int,
    val sql: String,
)
