package live.lingting.framework.mybatis.typehandler

import live.lingting.framework.util.StringUtils
import org.apache.ibatis.type.BaseTypeHandler
import org.apache.ibatis.type.JdbcType
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.sql.CallableStatement
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * @author lingting 2022/8/22 9:41
 */
class LocalTimeTypeHandler : BaseTypeHandler<LocalTime?>(), AutoRegisterTypeHandler<LocalTime?> {
    @Throws(SQLException::class)
    override fun setNonNullParameter(ps: PreparedStatement, i: Int, parameter: LocalTime?, jdbcType: JdbcType?) {
        if (parameter == null) {
            ps.setObject(i, null)
        } else if (jdbcType == null) {
            ps.setObject(i, format(parameter))
        } else {
            ps.setObject(i, format(parameter), jdbcType.TYPE_CODE)
        }
    }

    @Throws(SQLException::class)
    override fun getNullableResult(rs: ResultSet, columnName: String): LocalTime? {
        return parse(rs.getString(columnName))
    }

    @Throws(SQLException::class)
    override fun getNullableResult(rs: ResultSet, columnIndex: Int): LocalTime? {
        return parse(rs.getString(columnIndex))
    }

    @Throws(SQLException::class)
    override fun getNullableResult(cs: CallableStatement, columnIndex: Int): LocalTime? {
        return parse(cs.getString(columnIndex))
    }

    fun format(localDate: LocalTime): String {
        return localDate.format(FORMATTER)
    }

    fun parse(`val`: String): LocalTime? {
        if (!StringUtils.hasText(`val`)) {
            return null
        }

        try {
            return LocalTime.parse(`val`, FORMATTER)
        } catch (e: DateTimeParseException) {
            log.error("Unable to convert string [{}] to LocalTime! using LocalDateTime.toLocalTime", `val`, e)
            val dateTime: LocalDateTime = LocalDateTimeTypeHandler.Companion.parse(`val`) ?: return null
            return dateTime.toLocalTime()
        }
    }

    companion object {
        val FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        private val log: Logger = LoggerFactory.getLogger(LocalTimeTypeHandler::class.java)
    }
}
