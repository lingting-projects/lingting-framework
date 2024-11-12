package live.lingting.framework.mybatis.alias

import com.baomidou.mybatisplus.core.metadata.TableInfoHelper
import live.lingting.framework.util.AnnotationUtils
import java.util.concurrent.ConcurrentHashMap

/**
 * 表别名辅助类
 *
 * @author Hccake 2021/1/14
 * @version 1.0
 */
class TableAliasHelper private constructor() {
    init {
        throw UnsupportedOperationException("This is a utility class and cannot be instantiated")
    }

    companion object {
        private const val COMMA = ","

        private const val DOT = "."

        /**
         * 存储类对应的表别名
         */
        private val TABLE_ALIAS_CACHE: MutableMap<Class<*>, String> = ConcurrentHashMap()

        /**
         * 储存类对应的带别名的查询字段
         */
        private val TABLE_ALIAS_SELECT_COLUMNS_CACHE: MutableMap<Class<*>, String> = ConcurrentHashMap()

        /**
         * 带别名的查询字段sql
         *
         * @param clazz 实体类class
         * @return sql片段
         */
        fun tableAliasSelectSql(clazz: Class<*>): String {
            var tableAliasSelectSql = TABLE_ALIAS_SELECT_COLUMNS_CACHE[clazz]
            if (tableAliasSelectSql == null) {
                val tableAlias = tableAlias(clazz)

                val tableInfo = TableInfoHelper.getTableInfo(clazz)
                val allSqlSelect = tableInfo.allSqlSelect
                val split = allSqlSelect.split(COMMA.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val stringBuilder = StringBuilder()
                for (column in split) {
                    stringBuilder.append(tableAlias).append(DOT).append(column).append(COMMA)
                }
                stringBuilder.deleteCharAt(stringBuilder.length - 1)
                tableAliasSelectSql = stringBuilder.toString()

                TABLE_ALIAS_SELECT_COLUMNS_CACHE[clazz] = tableAliasSelectSql
            }
            return tableAliasSelectSql
        }

        /**
         * 获取实体类对应别名
         *
         * @param clazz 实体类
         * @return 表别名
         */
        fun tableAlias(clazz: Class<*>): String {
            var tableAlias = TABLE_ALIAS_CACHE[clazz]
            if (tableAlias == null) {
                val annotation = AnnotationUtils.findAnnotation(clazz, TableAlias::class.java) ?: throw TableAliasNotFoundException(
                    "[tableAliasSelectSql] No TableAlias annotations found in class: $clazz"
                )
                tableAlias = annotation.value
                TABLE_ALIAS_CACHE[clazz] = tableAlias
            }
            return tableAlias
        }
    }
}
