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
        val link = DingTalkParams.Link().also {
            it.text = text
            it.title = title
            it.picUrl = picUrl
            it.messageUrl = messageUrl
        }
        params.link = link
        return params
    }

}
