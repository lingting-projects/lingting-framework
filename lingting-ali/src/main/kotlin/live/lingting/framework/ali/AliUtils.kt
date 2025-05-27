package live.lingting.framework.ali

import live.lingting.framework.time.DatePattern
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * @author lingting 2024-09-14 13:42
 */
object AliUtils {
    @JvmField
    val CREDENTIAL_EXPIRE: Duration = Duration.ofHours(1)

    const val HEADER_ERR: String = "x-oss-err"

    const val HEADER_EC: String = "x-oss-ec"

    @JvmStatic
    @JvmOverloads
    fun parse(str: String, formatter: DateTimeFormatter = DatePattern.FORMATTER_ISO_8601): LocalDateTime {
        val parse = LocalDateTime.parse(str, formatter)
        val atGmt = parse.atZone(DatePattern.GMT_ZONE_ID)
        val atSystem = atGmt.withZoneSameInstant(DatePattern.SYSTEM_ZONE_ID)
        return atSystem.toLocalDateTime()
    }

    @JvmStatic
    @JvmOverloads
    fun format(dateTime: LocalDateTime, formatter: DateTimeFormatter = DatePattern.FORMATTER_ISO_8601): String {
        val atZone = dateTime.atZone(DatePattern.SYSTEM_ZONE_ID)
        val atGmt = atZone.withZoneSameInstant(DatePattern.GMT_ZONE_ID)
        return formatter.format(atGmt)
    }
}
