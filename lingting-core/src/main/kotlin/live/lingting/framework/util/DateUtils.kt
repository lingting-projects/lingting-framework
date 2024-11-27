package live.lingting.framework.util


import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.Date
import live.lingting.framework.time.DatePattern
import live.lingting.framework.util.LocalDateTimeUtils.timestamp
import live.lingting.framework.util.LocalDateTimeUtils.toLocalDateTime

/**
 * @author lingting 2022/11/28 10:12
 */
object DateUtils {

    // region source

    @JvmStatic
    @JvmOverloads
    fun parse(dateTime: LocalDateTime, offset: ZoneOffset = DatePattern.DEFAULT_ZONE_OFFSET): Date {
        val timestamp = dateTime.timestamp(offset)
        return Date(timestamp)
    }

    @JvmStatic
    @JvmOverloads
    fun parse(date: Date, offset: ZoneOffset = DatePattern.DEFAULT_ZONE_OFFSET): LocalDateTime {
        return date.time.toLocalDateTime(offset)
    }

    // endregion

    // region kt

    fun LocalDateTime.toDate(offset: ZoneOffset = DatePattern.DEFAULT_ZONE_OFFSET): Date {
        return parse(this, offset)
    }

    fun Date.toLocalDateTime(offset: ZoneOffset = DatePattern.DEFAULT_ZONE_OFFSET): LocalDateTime {
        return parse(this, offset)
    }

    // endregion

}