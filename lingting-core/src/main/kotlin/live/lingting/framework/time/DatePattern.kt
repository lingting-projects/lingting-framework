package live.lingting.framework.time

import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 * @author lingting
 */
object DatePattern {

    @JvmField
    val UTC8_ZONE_OFFSET: ZoneOffset = ZoneOffset.of("+8")

    @JvmField
    val UTC8_ZONE_ID: ZoneId = UTC8_ZONE_OFFSET.normalized()

    @JvmField
    val GMT_ZONE_OFFSET: ZoneOffset = ZoneOffset.of("+0")

    @JvmField
    val GMT_ZONE_ID: ZoneId = GMT_ZONE_OFFSET.normalized()

    @JvmField
    val SYSTEM_ZONE_ID: ZoneId = ZoneId.systemDefault()

    @JvmField
    val SYSTEM_ZONE_OFFSET: ZoneOffset = SYSTEM_ZONE_ID.rules.getOffset(Instant.now())

    const val NORM_DATETIME_PATTERN: String = "yyyy-MM-dd HH:mm:ss"

    const val MILLIS_NORM_DATETIME_PATTERN: String = "yyyy-MM-dd HH:mm:ss.SSS"

    const val NORM_DATE_PATTERN: String = "yyyy-MM-dd"

    const val NORM_TIME_PATTERN: String = "HH:mm:ss"

    const val MILLIS_NORM_TIME_PATTERN: String = "HH:mm:ss.SSS"

    const val ISO_8601_DATETIME_PATTERN: String = "yyyy-MM-dd'T'HH:mm:ss'Z'"

    const val MILLIS_ISO_8601_DATETIME_PATTERN: String = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

    @JvmField
    val FORMATTER_YMD_HMS: DateTimeFormatter = DateTimeFormatter.ofPattern(NORM_DATETIME_PATTERN)

    @JvmField
    val FORMATTER_YMD_HMS_MILLIS: DateTimeFormatter = DateTimeFormatter.ofPattern(MILLIS_NORM_DATETIME_PATTERN)

    @JvmField
    val FORMATTER_YMD: DateTimeFormatter = DateTimeFormatter.ofPattern(NORM_DATE_PATTERN)

    @JvmField
    val FORMATTER_HMS: DateTimeFormatter = DateTimeFormatter.ofPattern(NORM_TIME_PATTERN)

    @JvmField
    val FORMATTER_HMS_MILLIS: DateTimeFormatter = DateTimeFormatter.ofPattern(MILLIS_NORM_TIME_PATTERN)

    @JvmField
    val FORMATTER_ISO_8601: DateTimeFormatter = DateTimeFormatter.ofPattern(ISO_8601_DATETIME_PATTERN)

    @JvmField
    val FORMATTER_ISO_8601_MILLIS: DateTimeFormatter = DateTimeFormatter.ofPattern(MILLIS_ISO_8601_DATETIME_PATTERN)

}

