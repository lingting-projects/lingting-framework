package live.lingting.framework.ali

import com.fasterxml.jackson.annotation.JsonIgnore
import live.lingting.framework.http.HttpMethod
import live.lingting.framework.http.api.ApiRequest
import live.lingting.framework.id.Snowflake
import live.lingting.framework.util.StringUtils
import live.lingting.framework.value.WaitValue

/**
 * @author lingting 2024-09-14 13:49
 */
abstract class AliRequest : ApiRequest() {

    @JsonIgnore
    protected var snowflake: Snowflake = Snowflake(5, 7)

    @JsonIgnore
    protected val nonceValue: WaitValue<String> = WaitValue.of()

    abstract fun name(): String

    abstract fun version(): String

    override fun path(): String = ""

    fun nonce(): String {
        return nonceValue.compute {
            if (StringUtils.hasText(it)) {
                return@compute it
            }
            snowflake.nextStr()
        }!!
    }

    override fun method(): HttpMethod {
        return HttpMethod.POST
    }

    override fun onCall() {
        headers.contentType("application/json;charset=utf8")
    }

}
