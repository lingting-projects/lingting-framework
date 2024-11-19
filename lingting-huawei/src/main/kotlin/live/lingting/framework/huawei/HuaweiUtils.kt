package live.lingting.framework.huawei

import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 * @author lingting 2024-09-13 11:54
 */
class HuaweiUtils private constructor() {
    init {
        throw UnsupportedOperationException("This is a utility class and cannot be instantiated")
    }

    companion object {
        val FORMATTER: DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

        val TOKEN_EARLY_EXPIRE: Duration = Duration.ofMinutes(15)

        val CREDENTIAL_EXPIRE: Duration = Duration.ofDays(1)

        val CHARSET: Charset = StandardCharsets.UTF_8

        const val HEADER_DATE: String = "Date"

        fun parse(str: String, zone: ZoneOffset): LocalDateTime {
            val parse = LocalDateTime.parse(str, FORMATTER)
            return parse.plusSeconds(zone.totalSeconds.toLong())
        }

        fun format(dateTime: LocalDateTime): String {
            return format(dateTime, DateTimeFormatter.RFC_1123_DATE_TIME)
        }

        @JvmStatic
        fun parse(string: String): LocalDateTime {
            return parse(string, DateTimeFormatter.RFC_1123_DATE_TIME)
        }
    }
}
