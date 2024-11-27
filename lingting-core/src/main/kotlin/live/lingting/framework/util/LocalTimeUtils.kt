package live.lingting.framework.util

import java.time.LocalTime
import java.time.format.DateTimeFormatter
import live.lingting.framework.time.DatePattern

/**
 * @author lingting 2022/11/28 10:12
 */
object LocalTimeUtils {

    @JvmStatic
    fun parse(str: String, pattern: String): LocalTime {
        val formatter = DateTimeFormatter.ofPattern(pattern)
        return parse(str, formatter)
    }

    @JvmStatic
    fun parse(str: String, formatter: DateTimeFormatter = DatePattern.FORMATTER_HMS): LocalTime {
        return LocalTime.parse(str, formatter)
    }

    fun String.toLocalTime(pattern: String): LocalTime {
        return parse(this, pattern)
    }

    fun String.toLocalTime(formatter: DateTimeFormatter = DatePattern.FORMATTER_HMS): LocalTime {
        return parse(this, formatter)
    }

    @JvmStatic
    fun LocalTime.format(pattern: String): String {
        val formatter = DateTimeFormatter.ofPattern(pattern)
        return format(formatter)
    }

    @JvmStatic
    @JvmOverloads
    fun LocalTime.format(formatter: DateTimeFormatter = DatePattern.FORMATTER_HMS): String {
        return formatter.format(this)
    }

}
