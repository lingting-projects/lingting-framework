package live.lingting.framework.aws

import live.lingting.framework.http.header.HttpHeaders
import java.time.LocalDateTime

/**
 * @author lingting 2025/5/22 10:35
 */
abstract class AwsSigner(open val ak: String) {

    abstract fun signed(): Signed<*>

    abstract fun signed(time: LocalDateTime): Signed<*>

    open class Signed<S : AwsSigner>(
        open val signer: S,
        open val headers: HttpHeaders,
        open val bodyPayload: String,
        open val source: String,
        open val sign: String,
        open val authorization: String,
    )

}
