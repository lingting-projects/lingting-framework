package live.lingting.framework.aws

import live.lingting.framework.http.header.HttpHeaders
import live.lingting.framework.time.DateTime
import live.lingting.framework.value.multi.StringMultiValue
import java.time.Duration
import java.time.LocalDateTime

/**
 * @author lingting 2025/5/22 10:35
 */
abstract class AwsSigner<S : AwsSigner<S, R>, R : AwsSigner.Signed<S, R>>(open val ak: String) {

    open fun signed(): R = signed(DateTime.current())

    /**
     * header 签名
     * @param time: 签名时间
     */
    abstract fun signed(time: LocalDateTime): R

    abstract fun signed(time: LocalDateTime, bodyPayload: String): R

    /**
     * url 签名
     * @param time: 签名时间
     * @param expire: 过期时间
     */
    abstract fun signed(time: LocalDateTime, expire: LocalDateTime): R

    open fun signed(time: LocalDateTime, expire: LocalDateTime, bodyPayload: String): R {
        val duration = Duration.between(time, expire)
        return signed(time, duration, bodyPayload)
    }

    abstract fun signed(time: LocalDateTime, duration: Duration): R

    abstract fun signed(time: LocalDateTime, duration: Duration, bodyPayload: String): R

    open class Signed<S : AwsSigner<S, R>, R : Signed<S, R>>(
        open val signer: S,
        open val headers: HttpHeaders,
        open val params: StringMultiValue?,
        open val bodyPayload: String,
        open val source: String,
        open val sign: String,
        open val authorization: String,
    )

}
