package live.lingting.framework.aws

import live.lingting.framework.data.DataSize
import live.lingting.framework.time.DatePattern
import live.lingting.framework.time.DateTime
import live.lingting.framework.util.DigestUtils
import live.lingting.framework.util.StringUtils.base64
import live.lingting.framework.util.StringUtils.firstUpper
import live.lingting.framework.util.StringUtils.hex
import java.io.InputStream
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

    const val HEADER_MD5: String = "content-md5"

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
    fun toParamsKey(key: String): String {
        val split = key.split("-")
        return split.joinToString("-") { it.firstUpper() }
    }

    /**
     * 计算内容在请求头上的 contentMd5 值
     */
    @JvmStatic
    fun contentMd5(stream: InputStream): String {
        val hex = DigestUtils.md5Hex(stream)
        return contentMd5FromMd5(hex)
    }

    /**
     * 计算内容在请求头上的 contentMd5 值
     */
    @JvmStatic
    fun contentMd5(bytes: ByteArray): String {
        val hex = DigestUtils.md5Hex(bytes)
        return contentMd5FromMd5(hex)
    }

    /**
     * 从原始的md5 生成请求头上的 contentMd5 值
     */
    @JvmStatic
    fun contentMd5FromMd5(md5: String): String {
        val bytes = md5.hex()
        return bytes.base64()
    }

}
