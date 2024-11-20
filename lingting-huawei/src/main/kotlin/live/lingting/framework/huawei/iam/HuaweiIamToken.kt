package live.lingting.framework.huawei.iam

import java.time.Duration
import java.time.LocalDateTime

/**
 * @author lingting 2024-09-12 22:04
 */
data class HuaweiIamToken(
    val value: String,
    val expire: LocalDateTime,
    val issued: LocalDateTime,
) {

    fun duration(now: LocalDateTime): Duration {
        return Duration.between(now, expire)
    }

    fun isExpired(tokenEarlyExpire: Duration): Boolean {
        val now = LocalDateTime.now()
        return isExpired(tokenEarlyExpire, now)
    }

    fun isExpired(tokenEarlyExpire: Duration, now: LocalDateTime): Boolean {
        val duration = duration(now)
        return duration.compareTo(tokenEarlyExpire) < 1
    }
}
