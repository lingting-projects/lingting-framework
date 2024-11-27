package live.lingting.framework.aws.policy

import java.time.Duration

import java.time.LocalDateTime
import live.lingting.framework.time.DateTime

/**
 * @author lingting 2024-09-12 20:38
 */
class Credential(
    @JvmField val ak: String,
    @JvmField val sk: String,
    @JvmField val token: String?,
    @JvmField val expire: LocalDateTime
) {
    fun between(): Duration {
        val now = DateTime.current()
        return between(now)
    }

    /**
     * 计算指定时间到过期时间还需要花费的时间.
     * @return 如果小于等于0 表示 已经过期
     */
    fun between(now: LocalDateTime): Duration {
        return Duration.between(now, expire)
    }

    val isExpired: Boolean
        get() {
            val duration = between()
            return duration.isZero || duration.isNegative
        }
}
