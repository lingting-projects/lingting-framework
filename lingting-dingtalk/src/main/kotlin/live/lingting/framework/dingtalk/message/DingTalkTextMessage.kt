package live.lingting.framework.dingtalk.message

import live.lingting.framework.dingtalk.DingTalkParams
import live.lingting.framework.dingtalk.enums.MessageTypeEnum

/**
 * @author lingting 2020/6/10 22:13
 */
class DingTalkTextMessage : AbstractDingTalkMessage() {
    /**
     * 消息内容
     */
    var content: String? = null

    override val type: MessageTypeEnum = MessageTypeEnum.TEXT

    override fun put(params: DingTalkParams): DingTalkParams {
        val text = DingTalkParams.Text(content)
        params.text = text
        return params
    }

}
