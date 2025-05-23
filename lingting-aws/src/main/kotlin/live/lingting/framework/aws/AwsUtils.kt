package live.lingting.framework.aws

import live.lingting.framework.data.DataSize
import live.lingting.framework.time.DatePattern
import live.lingting.framework.time.DateTime
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 * @author lingting 2024-09-19 15:20
 */
object AwsUtils {

    @JvmField
    val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'")

    @JvmField
    val SCOPE_DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")

    /**
     * 10M
     */
    @JvmField
    val MULTIPART_DEFAULT_PART_SIZE = DataSize.ofMb(10)

    /**
     * 5G
     */
    @JvmField
    val MULTIPART_MAX_PART_SIZE = DataSize.ofGb(5)

    /**
     * 100K
     */
    @JvmField
    val MULTIPART_MIN_PART_SIZE = DataSize.ofKb(100)

    const val MULTIPART_MAX_PART_COUNT = 1000L

    const val PAYLOAD_UNSIGNED: String = "UNSIGNED-PAYLOAD"

    const val HEADER_PREFIX: String = "x-amz"

    const val HEADER_DATE: String = "$HEADER_PREFIX-date"

    const val HEADER_CONTENT_SHA256: String = "$HEADER_PREFIX-content-sha256"

    const val HEADER_TOKEN: String = "$HEADER_PREFIX-security-token"

    const val HEADER_ACL: String = "$HEADER_PREFIX-acl"

    const val HEADER_PREFIX_META: String = "$HEADER_PREFIX-meta-"

    @JvmStatic
    fun atZone(dateTime: LocalDateTime): ZonedDateTime {
        return dateTime.atZone(DateTime.zoneId)
    }

    @JvmStatic
    fun format(dateTime: ZonedDateTime, formatter: DateTimeFormatter): String {
        val atGmt = dateTime.withZoneSameInstant(DatePattern.GMT_ZONE_ID)
        return formatter.format(atGmt)
    }

    @JvmStatic
    fun format(dateTime: LocalDateTime, formatter: DateTimeFormatter): String {
        val atZone = atZone(dateTime)
        return format(atZone, formatter)
    }

    @JvmStatic
    fun atGmt(string: String, formatter: DateTimeFormatter): ZonedDateTime {
        val source = LocalDateTime.parse(string, formatter)
        return source.atZone(DatePattern.GMT_ZONE_ID)
    }

    @JvmStatic
    fun parse(atGmt: ZonedDateTime): LocalDateTime {
        val atZone = atGmt.withZoneSameInstant(DateTime.zoneId)
        return atZone.toLocalDateTime()
    }

    @JvmStatic
    fun parse(string: String, formatter: DateTimeFormatter): LocalDateTime {
        val atGmt = atGmt(string, formatter)
        return parse(atGmt)
    }

    @JvmStatic
    fun encode(s: String): String {
        return URLEncoder.encode(s, StandardCharsets.UTF_8)
    }

}
