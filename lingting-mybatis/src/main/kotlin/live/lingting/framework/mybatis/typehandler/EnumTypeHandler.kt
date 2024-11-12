package live.lingting.framework.mybatis.typehandler

import live.lingting.framework.util.EnumUtils
import org.apache.ibatis.type.BaseTypeHandler
import org.apache.ibatis.type.JdbcType
import java.sql.CallableStatement
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

/**
 * @author lingting 2022/12/14 16:06
 */
class EnumTypeHandler<E : Enum<E>?>(private val type: Class<E>) : BaseTypeHandler<E?>(), AutoRegisterTypeHandler<E> {
    @Throws(SQLException::class)
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

    @Throws(SQLException::class)
    override fun getNullableResult(rs: ResultSet, columnName: String): E? {
        return of(rs.getString(columnName))
    }

    @Throws(SQLException::class)
    override fun getNullableResult(rs: ResultSet, columnIndex: Int): E? {
        return of(rs.getString(columnIndex))
    }

    @Throws(SQLException::class)
    override fun getNullableResult(cs: CallableStatement, columnIndex: Int): E? {
        return of(cs.getString(columnIndex))
    }

    fun of(`val`: String): E? {
        for (e in type.enumConstants) {
            val value = EnumUtils.getValue(e)
            if ( // 值匹配
                `val` == value // 字符串值匹配
                || (value != null && value.toString() == `val`)
            ) {
                return e
            }
        }
        return null
    }
}
