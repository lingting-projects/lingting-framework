package live.lingting.framework.huawei.iam

import java.time.Duration
import java.time.LocalDateTime

/**
 * @author lingting 2024-09-12 22:04
 */
class HuaweiIamToken {
    var value: String? = null

    var expire: LocalDateTime? = null

    var issued: LocalDateTime? = null

    constructor()

    constructor(value: String?, expire: LocalDateTime?, issued: LocalDateTime?) {
        this.value = value
        this.expire = expire
        this.issued = issued
    }

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
