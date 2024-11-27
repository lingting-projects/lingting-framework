package live.lingting.framework.mybatis.typehandler

import java.sql.CallableStatement
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.time.LocalDate

import java.time.LocalDateTime
import live.lingting.framework.time.DateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import kotlin.math.max
import live.lingting.framework.util.StringUtils
import org.apache.ibatis.type.BaseTypeHandler
import org.apache.ibatis.type.JdbcType
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author lingting 2022/8/22 9:41
 */
class LocalDateTimeTypeHandler : BaseTypeHandler<LocalDateTime>(), AutoRegisterTypeHandler<LocalDateTime> {

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
        return localDate.format(FORMAT_NORMAL)
    }

    companion object {
        const val MICROSECONDS_DELIMITER: String = "."

        const val MICROSECONDS: String = "S"

        const val STR_FORMAT_NORMAL: String = "yyyy-MM-dd HH:mm:ss"

        val FORMAT_NORMAL: DateTimeFormatter = DateTimeFormatter.ofPattern(STR_FORMAT_NORMAL)

        private val CACHE: MutableMap<Int, DateTimeFormatter> = HashMap(16)
        private val log: Logger = LoggerFactory.getLogger(LocalDateTimeTypeHandler::class.java)

        fun parse(`val`: String?): LocalDateTime? {
            if (!StringUtils.hasText(`val`)) {
                return null
            }

            // 微秒处理
            if (`val`!!.contains(MICROSECONDS_DELIMITER)) {
                val number = `val`.length - `val`.indexOf(MICROSECONDS_DELIMITER) - 1

                val dateTimeFormatter = CACHE.computeIfAbsent(number) { k ->
                    val builder = STR_FORMAT_NORMAL + MICROSECONDS_DELIMITER + MICROSECONDS.repeat(max(0.0, number.toDouble()).toInt())
                    DateTimeFormatter.ofPattern(builder)
                }

                return LocalDateTime.parse(`val`, dateTimeFormatter)
            }

            // 数据类型声明紊乱处理
            try {
                return LocalDateTime.parse(`val`, FORMAT_NORMAL)
            } catch (e: DateTimeParseException) {
                log.error("Unable to convert string [{}] to LocalDataTime! using LocalDate.asStartOfDay", `val`, e)
                // 使用当天0点处理
                return LocalDate.parse(`val`, LocalDateTypeHandler.FORMATTER).atStartOfDay()
            }
        }
    }
}
