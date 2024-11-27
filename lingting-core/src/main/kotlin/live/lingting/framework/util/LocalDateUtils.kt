package live.lingting.framework.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import live.lingting.framework.time.DatePattern

/**
 * @author lingting 2022/11/28 10:12
 */
object LocalDateUtils {

    @JvmStatic
    fun parse(str: String, pattern: String): LocalDate {
        val formatter = DateTimeFormatter.ofPattern(pattern)
        return parse(str, formatter)
    }

    @JvmStatic
    @JvmOverloads
    fun parse(str: String, formatter: DateTimeFormatter = DatePattern.FORMATTER_YMD): LocalDate {
        return LocalDate.parse(str, formatter)
    }

    fun String.toLocalDate(pattern: String): LocalDate {
        return parse(this, pattern)
    }

    fun String.toLocalDate(formatter: DateTimeFormatter = DatePattern.FORMATTER_YMD): LocalDate {
        return parse(this, formatter)
    }

    @JvmStatic
    fun LocalDate.format(pattern: String): String {
        val formatter = DateTimeFormatter.ofPattern(pattern)
        return format(formatter)
    }

    @JvmStatic
    @JvmOverloads
    fun LocalDate.format(date: LocalDate, formatter: DateTimeFormatter = DatePattern.FORMATTER_YMD): String {
        return formatter.format(date)
    }


}
