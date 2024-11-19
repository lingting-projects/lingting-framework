package live.lingting.framework.mybatis.typehandler

import java.sql.CallableStatement
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import live.lingting.framework.util.StringUtils
import org.apache.ibatis.type.BaseTypeHandler
import org.apache.ibatis.type.JdbcType
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author lingting 2022/8/22 9:41
 */
class LocalDateTypeHandler : BaseTypeHandler<LocalDate?>(), AutoRegisterTypeHandler<LocalDate?> {

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
        return localDate.format(FORMATTER)
    }

    fun parse(`val`: String): LocalDate? {
        if (!StringUtils.hasText(`val`)) {
            return null
        }

        try {
            return LocalDate.parse(`val`, FORMATTER)
        } catch (e: DateTimeParseException) {
            log.error("Unable to convert string [{}] to LocalData! using LocalDateTime.toLocalData", `val`, e)
            val dateTime: LocalDateTime = LocalDateTimeTypeHandler.parse(`val`) ?: return null
            return dateTime.toLocalDate()
        }
    }

    companion object {
        val FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        private val log: Logger = LoggerFactory.getLogger(LocalDateTypeHandler::class.java)
    }
}
