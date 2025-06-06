package live.lingting.framework.mybatis.typehandler

import live.lingting.framework.time.DatePattern
import live.lingting.framework.util.StringUtils
import org.apache.ibatis.type.JdbcType
import java.sql.CallableStatement
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.time.LocalDateTime

/**
 * @author lingting 2022/8/22 9:41
 */
open class LocalDateTimeTypeHandler : AbstractAutoRegisterTypeHandler<LocalDateTime>() {

    companion object {

        @JvmStatic
        var formatter = DatePattern.FORMATTER_YMD_HMS

    }

    override fun setNonNullParameter(ps: PreparedStatement, i: Int, parameter: LocalDateTime?, jdbcType: JdbcType?) {
        if (parameter == null) {
            ps.setObject(i, null)
        } else if (jdbcType == null) {
            ps.setObject(i, format(parameter))
        } else {
            ps.setObject(i, format(parameter), jdbcType.TYPE_CODE)
        }
    }

    override fun getNullableResult(rs: ResultSet, columnName: String): LocalDateTime? {
        val source = rs.getString(columnName)
        if (!StringUtils.hasText(source)) {
            return null
        }
        return parse(source)
    }

    override fun getNullableResult(rs: ResultSet, columnIndex: Int): LocalDateTime? {
        val source = rs.getString(columnIndex)
        if (!StringUtils.hasText(source)) {
            return null
        }
        return parse(source)
    }

    override fun getNullableResult(cs: CallableStatement, columnIndex: Int): LocalDateTime? {
        val source = cs.getString(columnIndex)
        if (!StringUtils.hasText(source)) {
            return null
        }
        return parse(source)
    }

    fun format(localDate: LocalDateTime): String {
        return localDate.format(formatter)
    }

    fun parse(value: String): LocalDateTime? {
        if (!StringUtils.hasText(value)) {
            return null
        }

        return LocalDateTime.parse(value, formatter)
    }

}
