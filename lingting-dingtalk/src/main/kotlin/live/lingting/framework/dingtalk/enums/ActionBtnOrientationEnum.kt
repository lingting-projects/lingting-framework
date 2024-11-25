package live.lingting.framework.dingtalk.enums

import com.fasterxml.jackson.annotation.JsonValue

/**
 * 跳转 ActionCard 类型 消息的按钮排列方式
 * @author lingting 2020/6/10 23:44
 */
enum class ActionBtnOrientationEnum(
    @JsonValue val value: String,
    val text: String
) {
    /**
     * 按钮排列样式值 说明
     */
    VERTICAL("0", "按钮竖向排列"),

    HORIZONTAL("1", "按钮横向排列"),
}
