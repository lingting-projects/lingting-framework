package live.lingting.framework.aws

import live.lingting.framework.http.header.HttpHeaders
import live.lingting.framework.time.DateTime
import java.time.LocalDateTime

/**
 * @author lingting 2025/5/22 10:35
 */
abstract class AwsSigner {

    open fun signed(): Signed<*> {
        return signed(DateTime.current())
    }

    abstract fun signed(time: LocalDateTime): Signed<*>

    abstract fun signed(time: LocalDateTime, bodyPayload: String): Signed<*>

    open class Signed<S : AwsSigner>(
        open val signer: S,
        open val headers: HttpHeaders,
        open val source: String,
        open val sign: String,
        open val authorization: String,
    )

}
