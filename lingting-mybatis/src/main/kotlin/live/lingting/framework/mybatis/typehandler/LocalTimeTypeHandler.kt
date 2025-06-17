package live.lingting.framework.mybatis.typehandler

import live.lingting.framework.time.DatePattern
import live.lingting.framework.util.StringUtils
import org.apache.ibatis.type.JdbcType
import java.sql.CallableStatement
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.time.LocalTime

/**
 * @author lingting 2022/8/22 9:41
 */
open class LocalTimeTypeHandler : AbstractAutoRegisterTypeHandler<LocalTime>() {

    companion object {

        @JvmStatic
        var formatter = DatePattern.FORMATTER_HMS

    }

    override fun setNonNullParameter(ps: PreparedStatement, i: Int, parameter: LocalTime?, jdbcType: JdbcType?) {
        if (parameter == null) {
            ps.setObject(i, null)
        } else if (jdbcType == null) {
            ps.setObject(i, format(parameter))
        } else {
            ps.setObject(i, format(parameter), jdbcType.TYPE_CODE)
        }
    }

    override fun getNullableResult(rs: ResultSet, columnName: String): LocalTime? {
        return parse(rs.getString(columnName))
    }

    override fun getNullableResult(rs: ResultSet, columnIndex: Int): LocalTime? {
        return parse(rs.getString(columnIndex))
    }

    override fun getNullableResult(cs: CallableStatement, columnIndex: Int): LocalTime? {
        return parse(cs.getString(columnIndex))
    }

    fun format(localDate: LocalTime): String {
        return localDate.format(formatter)
    }

    fun parse(value: String): LocalTime? {
        if (!StringUtils.hasText(value)) {
            return null
        }

        return LocalTime.parse(value, formatter)
    }
}
