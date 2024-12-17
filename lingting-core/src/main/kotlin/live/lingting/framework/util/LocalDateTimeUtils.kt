package live.lingting.framework.util

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import live.lingting.framework.time.DatePattern
import live.lingting.framework.time.DateTime

/**
 * @author lingting 2022/11/28 10:12
 */
object LocalDateTimeUtils {

    /**
     * 字符串转时间
     * @param str yyyy-MM-dd HH:mm:ss 格式字符串
     * @return java.time.LocalDateTime 时间
     */
    @JvmStatic
    fun parse(str: String, pattern: String): LocalDateTime {
        val formatter = DateTimeFormatter.ofPattern(pattern)
        return parse(str, formatter)
    }

    @JvmStatic
    fun parse(str: String, formatter: DateTimeFormatter = DatePattern.FORMATTER_YMD_HMS): LocalDateTime {
        return LocalDateTime.parse(str, formatter)
    }

    /**
     * 时间戳转时间, 默认 使用 GMT+8 时区
     * @param timestamp 时间戳 - 毫秒
     * @return java.time.LocalDateTime
     */
    @JvmStatic
    @JvmOverloads
    fun parse(timestamp: Long, zoneId: ZoneId = DateTime.zoneId): LocalDateTime {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), zoneId)
    }

    fun String.toLocalDateTime(pattern: String): LocalDateTime {
        return parse(this, pattern)
    }

    fun String.toLocalDateTime(formatter: DateTimeFormatter = DatePattern.FORMATTER_YMD_HMS): LocalDateTime {
        return parse(this, formatter)
    }

    fun Long.toLocalDateTime(zoneId: ZoneId = DateTime.zoneId): LocalDateTime {
        return parse(this, zoneId)
    }

    inline val LocalDateTime.timestamp: Long get() = timestamp()

    @JvmStatic
    @JvmOverloads
    fun LocalDateTime.timestamp(offset: ZoneOffset = DateTime.zoneOffset): Long {
        return toInstant(offset).toEpochMilli()
    }

    @JvmStatic
    fun LocalDateTime.format(pattern: String): String {
        val formatter = DateTimeFormatter.ofPattern(pattern)
        return format(formatter)
    }

    @JvmStatic
    @JvmOverloads
    fun LocalDateTime.format(formatter: DateTimeFormatter = DatePattern.FORMATTER_YMD_HMS): String {
        return formatter.format(this)
    }

}
