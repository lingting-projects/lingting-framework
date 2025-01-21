package live.lingting.framework.mybatis.methods

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.core.metadata.TableInfo
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper
import com.baomidou.mybatisplus.core.toolkit.StringUtils
import com.baomidou.mybatisplus.core.toolkit.sql.SqlInjectionUtils
import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator
import org.apache.ibatis.executor.keygen.KeyGenerator
import org.apache.ibatis.executor.keygen.NoKeyGenerator
import org.apache.ibatis.mapping.MappedStatement

/**
 * 所有插入自定义方法的父类
 * @author lingting 2020/5/27 15:14
 */
abstract class AbstractInsert protected constructor(methodName: String) : AbstractMybatisMethod(methodName) {
    override fun injectMappedStatement(mapperClass: Class<*>, modelClass: Class<*>, tableInfo: TableInfo): MappedStatement {
        val prepare = prepare(tableInfo)
        val key = key(tableInfo)

        val sql: String = String.format(getSql(), tableInfo.tableName, prepare.columnSql, prepare.valueSql)
        val sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass)

        return addInsertMappedStatement(
            mapperClass, modelClass, getName(), sqlSource, key.generator,
            key.property, key.column
        )
    }

    protected fun getName(): String {
        return methodName
    }

    /**
     * 获取注册的脚本
     * @return java.lang.String
     */
    protected abstract fun getSql(): String

    protected fun prepare(tableInfo: TableInfo): PrepareResult {
        val columnMaybe = tableInfo.getAllInsertSqlColumnMaybeIf(null)
        val columnSql = SqlScriptUtils.convertTrim(columnMaybe, LEFT_BRACKET, RIGHT_BRACKET, null, COMMA)

        val valueMaybe = tableInfo.getAllInsertSqlPropertyMaybeIf(null)
        val valueSql = SqlScriptUtils.convertTrim(valueMaybe, LEFT_BRACKET, RIGHT_BRACKET, null, COMMA)

        return PrepareResult(columnSql, valueSql)
    }

    /**
     * mybatis 主键逻辑处理：主键生成策略，以及主键回填
     */
    protected fun key(tableInfo: TableInfo): KeyResult {
        var keyGenerator: KeyGenerator = NoKeyGenerator.INSTANCE
        var keyColumn: String? = null
        var keyProperty: String? = null
        // 表包含主键处理逻辑,如果不包含主键当普通字段处理
        if (StringUtils.isNotBlank(tableInfo.keyProperty)) {
            if (tableInfo.idType == IdType.AUTO) {
                /* 自增主键 */
                keyGenerator = Jdbc3KeyGenerator.INSTANCE
                // 去除转义符
                keyColumn = SqlInjectionUtils.removeEscapeCharacter(tableInfo.keyColumn)
                keyProperty = tableInfo.keyProperty
            } else if (null != tableInfo.keySequence) {
                keyGenerator = TableInfoHelper.genKeyGenerator(methodName, tableInfo, builderAssistant)
                keyColumn = tableInfo.keyColumn
                keyProperty = tableInfo.keyProperty
            }
        }
        return KeyResult(keyGenerator, keyColumn, keyProperty)
    }
}
