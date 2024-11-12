package live.lingting.framework.ali

import live.lingting.framework.time.DatePattern
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * @author lingting 2024-09-14 13:42
 */
class AliUtils private constructor() {
    init {
        throw UnsupportedOperationException("This is a utility class and cannot be instantiated")
    }

    companion object {
        val CREDENTIAL_EXPIRE: Duration = Duration.ofHours(1)

        const val HEADER_ERR: String = "x-oss-err"

        const val HEADER_EC: String = "x-oss-ec"
        private val log: Logger = LoggerFactory.getLogger(AliUtils::class.java)

        fun parse(str: String): LocalDateTime {
            val parse = LocalDateTime.parse(str, DatePattern.FORMATTER_ISO_8601)
            val atGmt = parse.atZone(DatePattern.GMT_ZONE_ID)
            val atSystem = atGmt.withZoneSameInstant(DatePattern.SYSTEM_ZONE_ID)
            return atSystem.toLocalDateTime()
        }

        fun format(dateTime: LocalDateTime, formatter: DateTimeFormatter): String {
            val atZone = dateTime.atZone(DatePattern.SYSTEM_ZONE_ID)
            val atGmt = atZone.withZoneSameInstant(DatePattern.GMT_ZONE_ID)
            return formatter.format(atGmt)
        }
    }
}
