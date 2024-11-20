package live.lingting.framework.ali.sts

import live.lingting.framework.ali.AliRequest
import live.lingting.framework.id.Snowflake
import live.lingting.framework.util.StringUtils
import live.lingting.framework.value.WaitValue

/**
 * @author lingting 2024-09-18 16:51
 */
abstract class AliStsRequest : AliRequest() {
    protected var snowflake: Snowflake = Snowflake(5, 7)

    protected val nonceValue: WaitValue<String> = WaitValue.of()

    abstract fun name(): String

    abstract fun version(): String


    fun nonce(): String {
        return nonceValue.compute {
            if (StringUtils.hasText(it)) {
                return@compute it
            }
            snowflake.nextStr()
        }!!
    }
}
