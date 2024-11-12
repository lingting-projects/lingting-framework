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
        private set

    /**
     * 内容
     */
    var text: MarkdownBuilder? = null
        private set

    /**
     * 按钮排列样式 默认横
     */
    var orientation: ActionBtnOrientationEnum = ActionBtnOrientationEnum.HORIZONTAL
        private set

    /**
     * 单个按钮的标题
     */
    var singleTitle: String? = null
        private set

    /**
     * 点击singleTitle按钮触发的URL
     */
    var singleUrl: String? = null
        private set

    /**
     * 自定义按钮组 如果配置了 按钮组 则 单按钮配置无效
     */
    private var buttons: MutableList<Button> = ArrayList()

    /**
     * 添加按钮
     */
    fun addButton(title: String?, url: String?): DingTalkActionCardMessage {
        buttons.add(Button(title, url))
        return this
    }

    override val type: MessageTypeEnum
        get() = MessageTypeEnum.ACTION_CARD

    override fun put(params: DingTalkParams): DingTalkParams {
        val card = ActionCard().setTitle(title)
            .setText(text!!.build())
            .setBtnOrientation(orientation.getVal())

        // 当 单按钮的 文本和链接都不为空时
        if (buttons.isEmpty()) {
            card.setSingleTitle(singleTitle).setSingleUrl(singleUrl)
        } else {
            card.setButtons(buttons)
        }
        return params.setActionCard(card)
    }

    fun getButtons(): List<Button> {
        return this.buttons
    }

    fun setTitle(title: String?): DingTalkActionCardMessage {
        this.title = title
        return this
    }

    fun setText(text: MarkdownBuilder?): DingTalkActionCardMessage {
        this.text = text
        return this
    }

    fun setOrientation(orientation: ActionBtnOrientationEnum): DingTalkActionCardMessage {
        this.orientation = orientation
        return this
    }

    fun setSingleTitle(singleTitle: String?): DingTalkActionCardMessage {
        this.singleTitle = singleTitle
        return this
    }

    fun setSingleUrl(singleUrl: String?): DingTalkActionCardMessage {
        this.singleUrl = singleUrl
        return this
    }

    fun setButtons(buttons: MutableList<Button>): DingTalkActionCardMessage {
        this.buttons = buttons
        return this
    }

    class Button(
        /**
         * 标题
         */
        val title: String?,
        /**
         * 跳转路径
         */
        val actionURL: String?
    )
}
