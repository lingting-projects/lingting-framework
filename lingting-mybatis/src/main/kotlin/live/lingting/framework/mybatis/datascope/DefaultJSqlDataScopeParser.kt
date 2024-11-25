package live.lingting.framework.mybatis.datascope

import java.util.Deque
import java.util.LinkedList
import java.util.function.Consumer
import live.lingting.framework.util.CollectionUtils
import net.sf.jsqlparser.expression.BinaryExpression
import net.sf.jsqlparser.expression.Expression
import net.sf.jsqlparser.expression.NotExpression
import net.sf.jsqlparser.expression.Parenthesis
import net.sf.jsqlparser.expression.operators.conditional.AndExpression
import net.sf.jsqlparser.expression.operators.conditional.OrExpression
import net.sf.jsqlparser.expression.operators.relational.ExistsExpression
import net.sf.jsqlparser.expression.operators.relational.InExpression
import net.sf.jsqlparser.schema.Table
import net.sf.jsqlparser.statement.delete.Delete
import net.sf.jsqlparser.statement.insert.Insert
import net.sf.jsqlparser.statement.select.FromItem
import net.sf.jsqlparser.statement.select.Join
import net.sf.jsqlparser.statement.select.ParenthesedFromItem
import net.sf.jsqlparser.statement.select.ParenthesedSelect
import net.sf.jsqlparser.statement.select.PlainSelect
import net.sf.jsqlparser.statement.select.Select
import net.sf.jsqlparser.statement.select.SelectItem
import net.sf.jsqlparser.statement.update.Update

/**
 * @author lingting 2024/11/25 14:40
 */
class DefaultJSqlDataScopeParser(scopes: List<JSqlDataScope>) : JSqlDataScopeParser(scopes) {

    override fun insert(insert: Insert, index: Int, sql: String) {
        // 不处理
    }

    override fun delete(delete: Delete, index: Int, sql: String) {
        val expression = delete.where
        val table = delete.table
        val injected = injectExpression(table, expression)
        delete.where = injected
    }

    override fun update(update: Update, index: Int, sql: String) {
        val expression = update.where
        val table = update.table
        val injected = injectExpression(table, expression)
        update.where = injected
    }

    override fun select(select: Select, index: Int, sql: String) {
        processSelect(select)
        val list = select.withItemsList
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(Consumer { select -> this.processSelect(select) })
        }
    }

    fun processSelect(select: Select?) {
        if (select == null) {
            return
        }

        // 普通查询
        if (select is PlainSelect) {
            processPlainSelect(select)
        } else if (select is ParenthesedSelect) {
            processSelect(select.select)
        }
    }

    fun processPlainSelect(plainSelect: PlainSelect) {
        // 处理所有查询项中的子查询
        val selectItems = plainSelect.selectItems
        if (!CollectionUtils.isEmpty(selectItems)) {
            selectItems.forEach(Consumer { item: SelectItem<*> -> this.processSelectItem(item) })
        }

        // 处理where条件中的查询
        processExpression(plainSelect.where)

        // 处理来源, 获取所有涉及到的表
        var tables = processFromItem(plainSelect.fromItem, true)

        // 处理join
        tables = processJoins(tables, plainSelect.joins)

        // 追加where条件
        if (!CollectionUtils.isEmpty(tables)) {
            val expression = injectExpression(tables, plainSelect.where)
            plainSelect.where = expression
        }
    }

    fun processSelectItem(item: SelectItem<*>) {
        val expression = item.expression
        if (expression is Select) {
            processSelect(expression)
        }
    }

    fun processExpression(expression: Expression?) {
        if (expression == null) {
            return
        }
        if (expression is FromItem) {
            processDeepFromItem(expression)
        }
        val isSelect = expression.toString().contains("SELECT")
        // 有子查询
        if (isSelect) {
            // 比较符号 , and , or , 等等
            if (expression is BinaryExpression) {
                processExpression(expression.leftExpression)
                processExpression(expression.rightExpression)
            } else if (expression is InExpression) {
                val right = expression.rightExpression
                if (right is Select) {
                    processSelect(right)
                }
            } else if (expression is ExistsExpression) {
                processExpression(expression.rightExpression)
            } else if (expression is NotExpression) {
                processExpression(expression.expression)
            } else if (expression is Parenthesis) {
                processExpression(expression.expression)
            }
        }
    }

    /**
     * 处理来源项
     * @param item 来源项
     * @param isDeep 是否深度处理
     * @return 查到的表
     */
    fun processFromItem(item: FromItem, isDeep: Boolean): MutableList<Table> {
        val list: MutableList<Table> = ArrayList()
        if (item is Table) {
            list.add(item)
        } else if (item is ParenthesedFromItem) {
            val lefts = processFromItem(item.fromItem, isDeep)
            val tables: List<Table> = processJoins(lefts, item.joins)
            list.addAll(tables)
        } else if (isDeep) {
            processDeepFromItem(item)
        }
        return list
    }

    fun processDeepFromItem(item: FromItem) {
        if (item is Select) {
            processSelect(item)
        }
    }

    fun processJoins(tables: MutableList<Table>, joins: List<Join>?): MutableList<Table> {
        var tables = tables
        if (joins.isNullOrEmpty()) {
            return tables
        }

        // 主表
        var mainTable: Table? = null
        // 左表
        var leftTable: Table? = null
        if (tables.size == 1) {
            mainTable = tables[0]
            leftTable = mainTable
        }

        // 对于 on 表达式写在最后的 join，需要记录下前面多个 on 的表名
        val onTableDeque: Deque<List<Table>> = LinkedList()

        for (join in joins) {
            val joinItem = join.rightItem

            // 涉及到的表
            val joinTables: List<Table> = processFromItem(joinItem, false)
            // 没有关联表, 深度处理该项
            if (joinTables.isEmpty()) {
                processDeepFromItem(joinItem)
                leftTable = null
            } else if (join.isSimple) {
                tables.addAll(joinTables)
            } else {
                // 当前表
                val joinTable = joinTables[0]

                var onTables: List<Table>? = null
                // 如果不要忽略，且是右连接，则记录下当前表
                if (join.isRight) {
                    mainTable = joinTable
                    if (leftTable != null) {
                        onTables = listOf(leftTable)
                    }
                } else if (join.isLeft) {
                    onTables = listOf<Table>(joinTable)
                } else if (join.isInner || join.astNode.jjtGetFirstToken().toString().equals("JOIN", ignoreCase = true)) {
                    onTables = if (mainTable == null) {
                        listOf<Table>(joinTable)
                    } else {
                        listOf(mainTable, joinTable)
                    }
                    mainTable = null
                }

                tables = ArrayList()
                if (mainTable != null) {
                    tables.add(mainTable)
                }

                // 获取 join 尾缀的 on 表达式列表
                val originOnExpressions = join.onExpressions
                // 正常 join on 表达式只有一个，立刻处理
                if (originOnExpressions.size == 1 && onTables != null) {
                    val onExpressions: MutableList<Expression> = LinkedList()
                    val injected = injectExpression(onTables, originOnExpressions.iterator().next())
                    onExpressions.add(injected!!)
                    join.onExpressions = onExpressions
                    leftTable = joinTable
                    continue
                }
                // 表名压栈，忽略的表压入 null，以便后续不处理
                onTableDeque.push(onTables)
                // 尾缀多个 on 表达式的时候统一处理
                if (originOnExpressions.size > 1) {
                    val onExpressions: MutableCollection<Expression> = LinkedList()
                    for (originOnExpression in originOnExpressions) {
                        val currentTableList = onTableDeque.poll()!!
                        if (CollectionUtils.isEmpty(currentTableList)) {
                            onExpressions.add(originOnExpression)
                        } else {
                            val injected = injectExpression(currentTableList, originOnExpression)
                            onExpressions.add(injected!!)
                        }
                    }
                    join.onExpressions = onExpressions
                }
                leftTable = joinTable
            }
        }
        return tables
    }

    fun injectExpression(table: Table, expression: Expression): Expression? {
        return injectExpression(listOf(table), expression)
    }

    fun injectExpression(tables: List<Table>, expression: Expression?): Expression? {
        val list: MutableList<Expression> = ArrayList(tables.size)
        // 生成数据权限条件
        for (table in tables) {
            // 获取表名
            val tableName: String = SqlParseUtils.getTableName(table.name)

            // 进行 dataScope 的表名匹配
            val matchDataScopes = scopes.filter { it.includes(tableName) }

            // 存在匹配成功的
            if (!CollectionUtils.isEmpty(matchDataScopes)) {
                // 计数
                matchScopes.addAll(matchDataScopes)
                // 参数构建
                val params = JSqlDataScopeParams(tableName, table.alias)
                // 获取到数据权限过滤的表达式
                matchDataScopes.map { it.handler(params) }
                    .filterNotNull()
                    .reduceOrNull { leftExpression, rightExpression -> AndExpression(leftExpression, rightExpression) }
                    ?.let { list.add(it) }
            }
        }

        if (list.isEmpty()) {
            return expression
        }

        var inject = list[0]

        for (i in 1 until list.size) {
            inject = AndExpression(inject, list[i])
        }

        if (expression == null) {
            return inject
        }

        val left = if (expression is OrExpression) Parenthesis(expression) else expression

        return AndExpression(left, inject)
    }
}

class DefaultJSqlDataScopeParserFactory : JSqlDataScopeParserFactory() {

    override fun create(scopes: List<JSqlDataScope>): JSqlDataScopeParser {
        return DefaultJSqlDataScopeParser(scopes)
    }
}
