package live.lingting.framework.aws

import live.lingting.framework.http.header.HttpHeaders
import live.lingting.framework.time.DateTime
import live.lingting.framework.value.multi.StringMultiValue
import java.time.LocalDateTime

/**
 * @author lingting 2025/5/22 10:35
 */
abstract class AwsSigner<S : AwsSigner<S, R>, R : AwsSigner.Signed<S, R>>(open val ak: String) {

    open fun signed(): R = signed(false)

    open fun signed(withUrl: Boolean): R = signed(DateTime.current(), withUrl)

    open fun signed(time: LocalDateTime): R = signed(time, false)

    /**
     * 当 withUrl = true 时. time 为过期时间
     */
    abstract fun signed(time: LocalDateTime, withUrl: Boolean): R

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
