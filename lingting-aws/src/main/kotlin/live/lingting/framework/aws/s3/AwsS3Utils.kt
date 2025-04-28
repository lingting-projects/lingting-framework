package live.lingting.framework.aws.s3

import live.lingting.framework.data.DataSize
import live.lingting.framework.time.DatePattern
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * @author lingting 2024-09-19 15:20
 */
object AwsS3Utils {
    /**
     * 10M
     */
    val MULTIPART_DEFAULT_PART_SIZE = DataSize.ofMb(10)

    /**
     * 5G
     */
    val MULTIPART_MAX_PART_SIZE = DataSize.ofGb(5)

    /**
     * 100K
     */
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
    fun format(dateTime: LocalDateTime, formatter: DateTimeFormatter): String {
        val atZone = dateTime.atZone(DatePattern.SYSTEM_ZONE_ID)
        val atGmt = atZone.withZoneSameInstant(DatePattern.GMT_ZONE_ID)
        return formatter.format(atGmt)
    }

    @JvmStatic
    fun parse(string: String, formatter: DateTimeFormatter): LocalDateTime {
        val source = LocalDateTime.parse(string, formatter)
        val atGmt = source.atZone(DatePattern.GMT_ZONE_ID)
        val atZone = atGmt.withZoneSameInstant(DatePattern.SYSTEM_ZONE_ID)
        return atZone.toLocalDateTime()
    }

    @JvmStatic
    fun encode(s: String): String {
        return URLEncoder.encode(s, StandardCharsets.UTF_8)
    }
}
