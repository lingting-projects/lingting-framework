package live.lingting.framework.mybatis.typehandler

import java.sql.CallableStatement
import java.sql.PreparedStatement
import java.sql.ResultSet
import live.lingting.framework.util.EnumUtils
import org.apache.ibatis.type.BaseTypeHandler
import org.apache.ibatis.type.JdbcType

/**
 * @author lingting 2022/12/14 16:06
 */
open class EnumTypeHandler<E : Enum<E>>(private val type: Class<E>) : BaseTypeHandler<E>(), AutoRegisterTypeHandler<E> {

    override fun setNonNullParameter(ps: PreparedStatement, i: Int, parameter: E?, jdbcType: JdbcType?) {
        val value = EnumUtils.getValue(parameter)
        if (jdbcType != null) {
            ps.setObject(i, value, jdbcType.TYPE_CODE)
        } else if (value is String) {
            ps.setString(i, value.toString())
        } else {
            ps.setObject(i, value)
        }
    }

    override fun getNullableResult(rs: ResultSet, columnName: String): E? {
        return of(rs.getString(columnName))
    }

    override fun getNullableResult(rs: ResultSet, columnIndex: Int): E? {
        return of(rs.getString(columnIndex))
    }

    override fun getNullableResult(cs: CallableStatement, columnIndex: Int): E? {
        return of(cs.getString(columnIndex))
    }

    fun of(v: String): E? {
        for (e in type.enumConstants) {
            val value = EnumUtils.getValue(e)
            if ( // 值匹配
                v == value
                // 字符串值匹配
                || (value != null && value.toString() == value)
            ) {
                return e
            }
        }
        return null
    }
}
