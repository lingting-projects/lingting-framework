package live.lingting.framework.aws.s3

import live.lingting.framework.time.DatePattern
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * @author lingting 2024-09-19 15:20
 */
class AwsS3Utils private constructor() {
    init {
        throw UnsupportedOperationException("This is a utility class and cannot be instantiated")
    }

    companion object {
        /**
         * 10M
         */
        const val MULTIPART_DEFAULT_PART_SIZE: Long = 10485760

        /**
         * 5G
         */
        const val MULTIPART_MAX_PART_SIZE: Long = 5368709120L

        /**
         * 100K
         */
        const val MULTIPART_MIN_PART_SIZE: Long = 102400

        const val MULTIPART_MAX_PART_COUNT: Long = 1000

        const val PAYLOAD_UNSIGNED: String = "UNSIGNED-PAYLOAD"

        const val HEADER_PREFIX: String = "x-amz"

        const val HEADER_DATE: String = HEADER_PREFIX + "-date"

        const val HEADER_CONTENT_SHA256: String = HEADER_PREFIX + "-content-sha256"

        const val HEADER_TOKEN: String = HEADER_PREFIX + "-security-token"

        const val HEADER_ACL: String = HEADER_PREFIX + "-acl"

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

        fun encode(s: String): String {
            return URLEncoder.encode(s, StandardCharsets.UTF_8)
        }
    }
}
