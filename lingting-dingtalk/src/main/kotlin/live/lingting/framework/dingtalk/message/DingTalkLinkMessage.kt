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
        private set

    /**
     * 标题
     */
    var title: String? = null
        private set

    /**
     * 图片url
     */
    var picUrl: String? = null
        private set

    /**
     * 消息链接
     */
    var messageUrl: String? = null
        private set

    override val type: MessageTypeEnum
        get() = MessageTypeEnum.LINK

    override fun put(params: DingTalkParams): DingTalkParams {
        return params.setLink(
            DingTalkParams.Link().setText(text).setTitle(title).setPicUrl(picUrl).setMessageUrl(messageUrl)
        )
    }

    fun setText(text: String?): DingTalkLinkMessage {
        this.text = text
        return this
    }

    fun setTitle(title: String?): DingTalkLinkMessage {
        this.title = title
        return this
    }

    fun setPicUrl(picUrl: String?): DingTalkLinkMessage {
        this.picUrl = picUrl
        return this
    }

    fun setMessageUrl(messageUrl: String?): DingTalkLinkMessage {
        this.messageUrl = messageUrl
        return this
    }
}
