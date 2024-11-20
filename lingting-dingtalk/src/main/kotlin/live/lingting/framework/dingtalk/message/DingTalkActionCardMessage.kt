package live.lingting.framework.dingtalk.message

import live.lingting.framework.MarkdownBuilder
import live.lingting.framework.dingtalk.DingTalkParams
import live.lingting.framework.dingtalk.DingTalkParams.ActionCard
import live.lingting.framework.dingtalk.enums.ActionBtnOrientationEnum
import live.lingting.framework.dingtalk.enums.MessageTypeEnum

/**
 * 跳转 ActionCard类型
 *
 * @author lingting 2020/6/10 23:39
 */
class DingTalkActionCardMessage : AbstractDingTalkMessage() {
    var title: String? = null


    /**
     * 内容
     */
    var markdown: MarkdownBuilder? = null


    /**
     * 按钮排列样式 默认横
     */
    var orientation: ActionBtnOrientationEnum = ActionBtnOrientationEnum.HORIZONTAL


    /**
     * 单个按钮的标题
     */
    var singleTitle: String? = null


    /**
     * 点击singleTitle按钮触发的URL
     */
    var singleUrl: String? = null


    /**
     * 自定义按钮组 如果配置了 按钮组 则 单按钮配置无效
     */
    private var buttons: MutableList<Button> = ArrayList()

    /**
     * 添加按钮
     */
    fun addButton(title: String, url: String): DingTalkActionCardMessage {
        buttons.add(Button(title, url))
        return this
    }

    override val type: MessageTypeEnum = MessageTypeEnum.ACTION_CARD

    override fun put(params: DingTalkParams): DingTalkParams {
        val card = ActionCard().apply {
            title = this@DingTalkActionCardMessage.title
            text = markdown?.build() ?: ""
            btnOrientation = orientation.value
        }

        // 当 单按钮的 文本和链接都不为空时
        if (buttons.isEmpty()) {
            card.singleTitle = singleTitle
            card.singleUrl = singleUrl
        } else {
            card.buttons = buttons
        }
        params.actionCard = card
        return params
    }


    data class Button(
        /**
         * 标题
         */
        val title: String,
        /**
         * 跳转路径
         */
        val actionURL: String
    )
}
