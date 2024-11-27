package live.lingting.framework.mybatis.typehandler

import java.sql.CallableStatement
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.time.LocalDate
import live.lingting.framework.time.DatePattern
import live.lingting.framework.util.StringUtils
import org.apache.ibatis.type.BaseTypeHandler
import org.apache.ibatis.type.JdbcType

/**
 * @author lingting 2022/8/22 9:41
 */
open class LocalDateTypeHandler : BaseTypeHandler<LocalDate>(), AutoRegisterTypeHandler<LocalDate> {

    companion object {

        @JvmStatic
        var formatter = DatePattern.FORMATTER_YMD

    }

    override fun setNonNullParameter(ps: PreparedStatement, i: Int, parameter: LocalDate?, jdbcType: JdbcType?) {
        if (parameter == null) {
            ps.setObject(i, null)
        } else if (jdbcType == null) {
            ps.setObject(i, format(parameter))
        } else {
            ps.setObject(i, format(parameter), jdbcType.TYPE_CODE)
        }
    }

    override fun getNullableResult(rs: ResultSet, columnName: String): LocalDate? {
        return parse(rs.getString(columnName))
    }

    override fun getNullableResult(rs: ResultSet, columnIndex: Int): LocalDate? {
        return parse(rs.getString(columnIndex))
    }

    override fun getNullableResult(cs: CallableStatement, columnIndex: Int): LocalDate? {
        return parse(cs.getString(columnIndex))
    }

    fun format(localDate: LocalDate): String {
        return localDate.format(formatter)
    }

    fun parse(value: String): LocalDate? {
        if (!StringUtils.hasText(value)) {
            return null
        }

        return LocalDate.parse(value, formatter)
    }

}
