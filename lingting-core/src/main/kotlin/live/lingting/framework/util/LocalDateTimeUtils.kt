package live.lingting.framework.util

import live.lingting.framework.time.DatePattern
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter


/**
 * @author lingting 2022/11/28 10:12
 */
class LocalDateTimeUtils private constructor() {
    init {
        throw UnsupportedOperationException("This is a utility class and cannot be instantiated")
    }

    companion object {
        // region LocalDateTime
        /**
         * 字符串转时间
         *
         * @param str yyyy-MM-dd HH:mm:ss 格式字符串
         * @return java.time.LocalDateTime 时间
         */
        fun parse(str: String): LocalDateTime {
            return LocalDateTime.parse(str, DatePattern.FORMATTER_YMD_HMS)
        }

        /**
         * 时间戳转时间
         *
         * @param timestamp 时间戳 - 毫秒
         * @param zoneId    时区
         * @return java.time.LocalDateTime
         */
        /**
         * 时间戳转时间, 使用 GMT+8 时区
         *
         * @param timestamp 时间戳 - 毫秒
         * @return java.time.LocalDateTime
         */

        fun parse(timestamp: Long, zoneId: ZoneId = DatePattern.DEFAULT_ZONE_ID): LocalDateTime {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), zoneId)
        }


        fun toTimestamp(dateTime: LocalDateTime, offset: ZoneOffset? = DatePattern.DEFAULT_ZONE_OFFSET): Long {
            return dateTime.toInstant(offset).toEpochMilli()
        }

        fun format(dateTime: LocalDateTime, formatter: String): String {
            return format(dateTime, DateTimeFormatter.ofPattern(formatter))
        }


        fun format(dateTime: LocalDateTime, formatter: DateTimeFormatter = DatePattern.FORMATTER_YMD_HMS): String {
            return formatter.format(dateTime)
        }

        // endregion
        // region LocalDate
        fun parseDate(str: String): LocalDate {
            return LocalDate.parse(str, DatePattern.FORMATTER_YMD)
        }

        fun format(date: LocalDate, formatter: String): String {
            return format(date, DateTimeFormatter.ofPattern(formatter))
        }


        fun format(date: LocalDate, formatter: DateTimeFormatter = DatePattern.FORMATTER_YMD): String {
            return formatter.format(date)
        }

        // endregion
        // region LocalTime
        fun parseTime(str: String): LocalTime {
            return LocalTime.parse(str, DatePattern.FORMATTER_HMS)
        }

        fun format(time: LocalTime, formatter: String): String {
            return format(time, DateTimeFormatter.ofPattern(formatter))
        }


        fun format(time: LocalTime, formatter: DateTimeFormatter = DatePattern.FORMATTER_HMS): String {
            return formatter.format(time)
        }

        // endregion
        // region Date
        fun toDate(dateTime: LocalDateTime): Date {
            val timestamp = toTimestamp(dateTime)
            return Date(timestamp)
        }

        fun parse(date: Date): LocalDateTime {
            return parse(date.time)
        } // endregion
    }
}
