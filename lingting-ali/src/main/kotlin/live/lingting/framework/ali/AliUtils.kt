package live.lingting.framework.ali

import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import live.lingting.framework.time.DatePattern

/**
 * @author lingting 2024-09-14 13:42
 */
object AliUtils {
    @JvmField
    val CREDENTIAL_EXPIRE: Duration = Duration.ofHours(1)

    const val HEADER_ERR: String = "x-oss-err"

    const val HEADER_EC: String = "x-oss-ec"

    @JvmStatic
    fun parse(str: String): LocalDateTime {
        val parse = LocalDateTime.parse(str, DatePattern.FORMATTER_ISO_8601)
        val atGmt = parse.atZone(DatePattern.GMT_ZONE_ID)
        val atSystem = atGmt.withZoneSameInstant(DatePattern.SYSTEM_ZONE_ID)
        return atSystem.toLocalDateTime()
    }

    @JvmStatic
    fun format(dateTime: LocalDateTime, formatter: DateTimeFormatter): String {
        val atZone = dateTime.atZone(DatePattern.SYSTEM_ZONE_ID)
        val atGmt = atZone.withZoneSameInstant(DatePattern.GMT_ZONE_ID)
        return formatter.format(atGmt)
    }
}
