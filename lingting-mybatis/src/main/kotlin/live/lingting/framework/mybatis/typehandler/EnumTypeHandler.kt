package live.lingting.framework.mybatis.typehandler

import live.lingting.framework.util.EnumUtils
import org.apache.ibatis.type.JdbcType
import java.sql.CallableStatement
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.util.Objects

/**
 * @author lingting 2022/12/14 16:06
 */
open class EnumTypeHandler<E : Enum<E>>(private val type: Class<E>) : AbstractTypeHandler<E>(),
    AutoRegisterTypeHandler<E> {

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

    fun of(v: String?): E? {
        return type.enumConstants.firstOrNull { e ->
            val value = EnumUtils.getValue(e)
            // 值匹配, 或者字符串值匹配
            v == value || Objects.equals(value?.toString(), v)
        }
    }
}
