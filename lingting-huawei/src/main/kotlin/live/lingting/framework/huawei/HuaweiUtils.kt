package live.lingting.framework.huawei

import live.lingting.framework.aws.AwsUtils
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 * @author lingting 2024-09-13 11:54
 */
object HuaweiUtils {

    @JvmField
    val FORMATTER: DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    @JvmField
    val TOKEN_EARLY_EXPIRE: Duration = Duration.ofMinutes(15)

    @JvmField
    val CREDENTIAL_EXPIRE: Duration = Duration.ofDays(1)

    @JvmField
    val CHARSET: Charset = StandardCharsets.UTF_8

    const val HEADER_DATE: String = "Date"

    @JvmStatic
    fun parse(str: String, zone: ZoneOffset): LocalDateTime {
        val parse = LocalDateTime.parse(str, FORMATTER)
        return parse.plusSeconds(zone.totalSeconds.toLong())
    }

    @JvmStatic
    fun format(dateTime: LocalDateTime): String {
        return AwsUtils.format(dateTime, DateTimeFormatter.RFC_1123_DATE_TIME)
    }

    @JvmStatic
    fun parse(string: String): LocalDateTime {
        return AwsUtils.parse(string, DateTimeFormatter.RFC_1123_DATE_TIME)
    }

}
