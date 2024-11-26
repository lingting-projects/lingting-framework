package live.lingting.framework.dingtalk.message

import live.lingting.framework.dingtalk.DingTalkParams
import live.lingting.framework.dingtalk.DingTalkParams.Markdown
import live.lingting.framework.dingtalk.enums.MessageTypeEnum
import live.lingting.framework.markdown.MarkdownBuilder

/**
 * @author lingting 2020/6/10 22:13
 */
class DingTalkMarkDownMessage : AbstractDingTalkMessage() {
    /**
     * 标题
     */
    var title: String? = null

    /**
     * 内容
     */
    var markdown: MarkdownBuilder? = null


    override val type: MessageTypeEnum = MessageTypeEnum.MARKDOWN

    override fun put(params: DingTalkParams): DingTalkParams {
        val mk = Markdown(title, markdown?.build())
        params.markdown = mk
        return params
    }

}
