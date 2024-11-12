package live.lingting.framework.dingtalk.message

import live.lingting.framework.MarkdownBuilder
import live.lingting.framework.dingtalk.DingTalkParams
import live.lingting.framework.dingtalk.DingTalkParams.Markdown
import live.lingting.framework.dingtalk.enums.MessageTypeEnum

/**
 * @author lingting 2020/6/10 22:13
 */
class DingTalkMarkDownMessage : AbstractDingTalkMessage() {
    /**
     * 标题
     */
    var title: String? = null
        private set

    /**
     * 内容
     */
    var text: MarkdownBuilder? = null
        private set

    override val type: MessageTypeEnum
        get() = MessageTypeEnum.MARKDOWN

    override fun put(params: DingTalkParams): DingTalkParams {
        return params.setMarkdown(Markdown().setTitle(title).setText(text!!.build()))
    }

    fun setTitle(title: String?): DingTalkMarkDownMessage {
        this.title = title
        return this
    }

    fun setText(text: MarkdownBuilder?): DingTalkMarkDownMessage {
        this.text = text
        return this
    }
}
