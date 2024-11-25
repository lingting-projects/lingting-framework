package live.lingting.framework.dingtalk

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import live.lingting.framework.jackson.JacksonUtils

/**
 * 钉钉返回信息
 * @author lingting 2020/6/11 0:23
 */
class DingTalkResponse {
    @JsonProperty("errcode")
    var code: Long? = null

    /**
     * 值为ok表示无异常
     */
    @JsonProperty("errmsg")
    var message: String? = null

    /**
     * 钉钉返回信息
     */
    var response: String = ""

    /**
     * 是否发送成功
     */
    var isSuccess: Boolean = false

    override fun toString(): String {
        return response
    }

    companion object {
        const val SUCCESS_CODE: Long = 0L

        @JsonCreator
        @JvmStatic
        fun of(res: String?): DingTalkResponse {
            if (res == null) {
                return DingTalkResponse()
            }
            val value = JacksonUtils.toObj(res, DingTalkResponse::class.java)
            value.response = res
            value.isSuccess = SUCCESS_CODE == value.code
            return value
        }
    }
}
