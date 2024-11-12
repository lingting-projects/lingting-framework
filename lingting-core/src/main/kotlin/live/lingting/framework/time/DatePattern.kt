package live.lingting.framework.time

import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 * @author lingting
 */
class DatePattern private constructor() {
    init {
        throw UnsupportedOperationException("This is a utility class and cannot be instantiated")
    }

    companion object {

        val DEFAULT_ZONE_OFFSET: ZoneOffset = ZoneOffset.of("+8")

        val DEFAULT_ZONE_ID: ZoneId = DEFAULT_ZONE_OFFSET.normalized()

        val GMT_ZONE_OFFSET: ZoneOffset = ZoneOffset.of("+0")


        val GMT_ZONE_ID: ZoneId = GMT_ZONE_OFFSET.normalized()


        val SYSTEM_ZONE_ID: ZoneId = ZoneId.systemDefault()

        val SYSTEM_ZONE_OFFSET: ZoneOffset = SYSTEM_ZONE_ID.rules.getOffset(Instant.now())

        const val NORM_DATETIME_PATTERN: String = "yyyy-MM-dd HH:mm:ss"

        const val NORM_DATE_PATTERN: String = "yyyy-MM-dd"

        const val NORM_TIME_PATTERN: String = "HH:mm:ss"

        const val ISO_8601_DATETIME_PATTERN: String = "yyyy-MM-dd'T'HH:mm:ss'Z'"

        val FORMATTER_YMD_HMS: DateTimeFormatter = DateTimeFormatter.ofPattern(NORM_DATETIME_PATTERN)

        val FORMATTER_YMD: DateTimeFormatter = DateTimeFormatter.ofPattern(NORM_DATE_PATTERN)

        val FORMATTER_HMS: DateTimeFormatter = DateTimeFormatter.ofPattern(NORM_TIME_PATTERN)


        val FORMATTER_ISO_8601: DateTimeFormatter = DateTimeFormatter.ofPattern(ISO_8601_DATETIME_PATTERN)
    }
}
