package live.lingting.framework.datascope.parser

import live.lingting.framework.datascope.JsqlDataScope
import live.lingting.framework.datascope.exception.DataScopeException
import live.lingting.framework.datascope.holder.DataScopeHolder
import live.lingting.framework.kt.logger
import net.sf.jsqlparser.parser.CCJSqlParserUtil
import net.sf.jsqlparser.statement.Statement
import net.sf.jsqlparser.statement.Statements
import net.sf.jsqlparser.statement.delete.Delete
import net.sf.jsqlparser.statement.insert.Insert
import net.sf.jsqlparser.statement.select.Select
import net.sf.jsqlparser.statement.update.Update

/**
 * @author lingting 2024-01-19 15:48
 */
abstract class DataScopeParser {
    protected val log = logger()

    fun parser(sql: String?, scopes: List<JsqlDataScope>, isMulti: Boolean): String {
        try {
            DataScopeHolder.push(scopes)
            val statements = parser(sql, isMulti)

            val builder = StringBuilder()

            for (i in statements!!.indices) {
                if (i > 0) {
                    builder.append(";")
                }

                val statement = statements[0]
                val parser = parser(statement, i, sql)
                builder.append(parser)
            }

            return builder.toString()
        } finally {
            DataScopeHolder.poll()
        }
    }

    fun parserSingle(sql: String?, scopes: List<JsqlDataScope>): String {
        return parser(sql, scopes, false)
    }

    fun parserMulti(sql: String?, scopes: List<JsqlDataScope>): String {
        return parser(sql, scopes, true)
    }

    protected fun parser(sql: String?, isMulti: Boolean): Statements? {
        try {
            if (isMulti) {
                return CCJSqlParserUtil.parseStatements(sql)
            } else {
                val statement = CCJSqlParserUtil.parse(sql)
                val statements = Statements()
                statements.add(statement)
                return statements
            }
        } catch (e: Exception) {
            throw DataScopeException("sql parse exception!", e)
        }
    }

    /**
     * 执行 SQL 解析
     * @param statement JsqlParser Statement
     * @return sql
     */
    protected fun parser(statement: Statement, index: Int, sql: String?): String {
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
    protected open fun insert(insert: Insert?, index: Int, sql: String?) {
        throw UnsupportedOperationException()
    }

    /**
     * 删除
     */
    protected open fun delete(delete: Delete, index: Int, sql: String?) {
        throw UnsupportedOperationException()
    }

    /**
     * 更新
     */
    protected open fun update(update: Update, index: Int, sql: String?) {
        throw UnsupportedOperationException()
    }

    /**
     * 查询
     */
    protected open fun select(select: Select, index: Int, sql: String?) {
        throw UnsupportedOperationException()
    }
}
