package live.lingting.framework.datascope.util

import net.sf.jsqlparser.expression.Alias
import net.sf.jsqlparser.schema.Column
import net.sf.jsqlparser.schema.Table

/**
 * SQL 解析工具类
 *
 * @author hccake
 */
class SqlParseUtils private constructor() {
    init {
        throw UnsupportedOperationException("This is a utility class and cannot be instantiated")
    }

    companion object {
        const val MYSQL_ESCAPE_CHARACTER: String = "`"

        /**
         * 兼容 mysql 转义表名 `t_xxx`
         *
         * @param tableName 表名
         * @return 去除转移字符后的表名
         */
        fun getTableName(tableName: String): String {
            var tableName = tableName
            if (tableName.startsWith(MYSQL_ESCAPE_CHARACTER) && tableName.endsWith(MYSQL_ESCAPE_CHARACTER)) {
                tableName = tableName.substring(1, tableName.length - 1)
            }
            return tableName
        }

        /**
         * 根据当前表是否有别名，动态对字段名前添加表别名 eg. 表名： table_1 as t 原始字段：column1 返回： t.column1
         *
         * @param table      表信息
         * @param columnName 字段名
         * @return 原始字段名，或者添加了表别名的字段名
         */
        fun getAliasColumn(table: Table, columnName: String?): Column {
            return getAliasColumn(table.name, table.alias, columnName)
        }

        /**
         * 根据当前表是否有别名，动态对字段名前添加表别名 eg. 表名： table_1 as t 原始字段：column1 返回： t.column1
         *
         * @param tableName  表名
         * @param tableAlias 别别名
         * @param columnName 字段名
         * @return 原始字段名，或者添加了表别名的字段名
         */
        @JvmStatic
        fun getAliasColumn(tableName: String?, tableAlias: Alias?, columnName: String?): Column {
            val columnBuilder = StringBuilder()
            // 为了兼容隐式内连接，没有别名时条件就需要加上表名
            if (tableAlias != null) {
                columnBuilder.append(tableAlias.name)
            } else {
                columnBuilder.append(tableName)
            }
            columnBuilder.append(".").append(columnName)
            return Column(columnBuilder.toString())
        }
    }
}
