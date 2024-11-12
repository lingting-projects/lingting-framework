package live.lingting.framework.dingtalk

import com.fasterxml.jackson.annotation.JsonProperty
import live.lingting.framework.jackson.JacksonUtils

/**
 * 钉钉返回信息
 *
 * @author lingting 2020/6/11 0:23
 */
class DingTalkResponse {
    @JsonProperty("errcode")
    var code: Long? = null
        private set

    /**
     * 值为ok表示无异常
     */
    @JsonProperty("errmsg")
    var message: String? = null
        private set

    /**
     * 钉钉返回信息
     */
    var response: String? = null
        private set

    /**
     * 是否发送成功
     */
    var isSuccess: Boolean = false
        private set

    override fun toString(): String {
        return response!!
    }

    @JsonProperty("errcode")
    fun setCode(code: Long?): DingTalkResponse {
        this.code = code
        return this
    }

    @JsonProperty("errmsg")
    fun setMessage(message: String?): DingTalkResponse {
        this.message = message
        return this
    }

    fun setResponse(response: String?): DingTalkResponse {
        this.response = response
        return this
    }

    fun setSuccess(success: Boolean): DingTalkResponse {
        this.isSuccess = success
        return this
    }

    companion object {
        const val SUCCESS_CODE: Long = 0L

        fun of(res: String?): DingTalkResponse {
            val value = JacksonUtils.toObj(res!!, DingTalkResponse::class.java)
            value.setResponse(res)
            value.setSuccess(SUCCESS_CODE == value.code)
            return value
        }
    }
}
