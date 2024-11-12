package live.lingting.framework.dingtalk.message

/**
 * @author lingting 2020/6/11 21:58
 */
interface DingTalkMessage {
    /**
     * 生成钉钉消息发送参数
     * @return 钉钉文档要求的 jsonString
     */
    fun generate(): String?
}
