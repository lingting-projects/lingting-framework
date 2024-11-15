package live.lingting.framework.dingtalk.enums

/**
 * 钉钉消息类型
 *
 * @author lingting 2020/6/10 21:29
 */
enum class MessageTypeEnum(val `val`: String, val desc: String) {
    /**
     * 消息值 消息说明
     */
    TEXT("text", "文本"),

    LINK("link", "链接"),

    MARKDOWN("markdown", "markdown"),

    ACTION_CARD("actionCard", "跳转 actionCard 类型"),
}