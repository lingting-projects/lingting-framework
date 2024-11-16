package live.lingting.framework.util

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Date
import live.lingting.framework.time.DatePattern


/**
 * @author lingting 2022/11/28 10:12
 */
object LocalDateTimeUtils {
    // region LocalDateTime
    /**
     * 字符串转时间
     *
     * @param str yyyy-MM-dd HH:mm:ss 格式字符串
     * @return java.time.LocalDateTime 时间
     */
    @JvmStatic
    fun parse(str: String): LocalDateTime {
        return LocalDateTime.parse(str, DatePattern.FORMATTER_YMD_HMS)
    }

    /**
     * 时间戳转时间, 默认 使用 GMT+8 时区
     *
     * @param timestamp 时间戳 - 毫秒
     * @return java.time.LocalDateTime
     */
    @JvmStatic
    @JvmOverloads
    fun parse(timestamp: Long, zoneId: ZoneId = DatePattern.DEFAULT_ZONE_ID): LocalDateTime {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), zoneId)
    }

    @JvmStatic
    @JvmOverloads
    fun toTimestamp(dateTime: LocalDateTime, offset: ZoneOffset = DatePattern.DEFAULT_ZONE_OFFSET): Long {
        return dateTime.toInstant(offset).toEpochMilli()
    }

    @JvmStatic
    fun format(dateTime: LocalDateTime, formatter: String): String {
        return format(dateTime, DateTimeFormatter.ofPattern(formatter))
    }


    @JvmStatic
    @JvmOverloads
    fun format(dateTime: LocalDateTime, formatter: DateTimeFormatter = DatePattern.FORMATTER_YMD_HMS): String {
        return formatter.format(dateTime)
    }

    // endregion
    // region LocalDate
    @JvmStatic
    fun parseDate(str: String): LocalDate {
        return LocalDate.parse(str, DatePattern.FORMATTER_YMD)
    }

    @JvmStatic
    fun format(date: LocalDate, formatter: String): String {
        return format(date, DateTimeFormatter.ofPattern(formatter))
    }

    @JvmStatic
    @JvmOverloads
    fun format(date: LocalDate, formatter: DateTimeFormatter = DatePattern.FORMATTER_YMD): String {
        return formatter.format(date)
    }

    // endregion
    // region LocalTime
    @JvmStatic
    fun parseTime(str: String): LocalTime {
        return LocalTime.parse(str, DatePattern.FORMATTER_HMS)
    }

    @JvmStatic
    fun format(time: LocalTime, formatter: String): String {
        return format(time, DateTimeFormatter.ofPattern(formatter))
    }


    @JvmStatic
    @JvmOverloads
    fun format(time: LocalTime, formatter: DateTimeFormatter = DatePattern.FORMATTER_HMS): String {
        return formatter.format(time)
    }

    // endregion
    // region Date
    @JvmStatic
    fun toDate(dateTime: LocalDateTime): Date {
        val timestamp = toTimestamp(dateTime)
        return Date(timestamp)
    }

    @JvmStatic
    fun parse(date: Date): LocalDateTime {
        return parse(date.time)
    }
    // endregion

}
