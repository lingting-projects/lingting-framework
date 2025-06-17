package live.lingting.framework.dingtalk.message

import live.lingting.framework.dingtalk.DingTalkParams
import live.lingting.framework.dingtalk.enums.MessageTypeEnum

/**
 * 钉钉消息基础类
 * @author lingting 2020/6/10 21:28
 */
abstract class AbstractDingTalkMessage : DingTalkMessage {
    /**
     * at 的人的手机号码
     */
    private val atPhones: MutableSet<String> = HashSet()

    /**
     * 是否 at 所有人
     */
    private var atAll = false

    fun atAll(): AbstractDingTalkMessage {
        atAll = true
        return this
    }

    /**
     * 添加 at 对象的手机号
     */
    fun addPhone(phone: String): AbstractDingTalkMessage {
        atPhones.add(phone)
        return this
    }

    /**
     * 获取消息类型
     * @return 返回消息类型
     */
    abstract val type: MessageTypeEnum

    /**
     * 设置非公有属性
     * @param params 已设置完公有参数的参数类
     * @return 已设置完成的参数类
     */
    abstract fun put(params: DingTalkParams): DingTalkParams

    override fun generate(): String {
        val params = put(
            DingTalkParams().also {
                it.type = type.value
                if (atAll) it.atAll()
                if (atPhones.isNotEmpty()) it.atPhones(atPhones)
            }
        )
        return params.json()
    }
}
