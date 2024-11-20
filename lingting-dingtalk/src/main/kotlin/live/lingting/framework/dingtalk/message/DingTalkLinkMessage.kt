package live.lingting.framework.dingtalk.message

import live.lingting.framework.dingtalk.DingTalkParams
import live.lingting.framework.dingtalk.enums.MessageTypeEnum

/**
 * @author lingting 2020/6/10 22:13
 */
class DingTalkLinkMessage : AbstractDingTalkMessage() {
    /**
     * 文本
     */
    var text: String? = null


    /**
     * 标题
     */
    var title: String? = null


    /**
     * 图片url
     */
    var picUrl: String? = null


    /**
     * 消息链接
     */
    var messageUrl: String? = null

    override val type: MessageTypeEnum = MessageTypeEnum.LINK

    override fun put(params: DingTalkParams): DingTalkParams {
        val link = DingTalkParams.Link().apply {
            text = this@DingTalkLinkMessage.text
            title = this@DingTalkLinkMessage.title
            picUrl = this@DingTalkLinkMessage.picUrl
            messageUrl = this@DingTalkLinkMessage.messageUrl
        }
        params.link = link
        return params
    }

}
