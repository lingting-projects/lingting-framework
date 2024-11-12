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
        private set

    override val type: MessageTypeEnum
        get() = MessageTypeEnum.TEXT

    override fun put(params: DingTalkParams): DingTalkParams {
        return params.setText(DingTalkParams.Text().setContent(content))
    }

    fun setContent(content: String?): DingTalkTextMessage {
        this.content = content
        return this
    }
}
