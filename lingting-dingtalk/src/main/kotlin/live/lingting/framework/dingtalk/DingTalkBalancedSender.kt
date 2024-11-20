package live.lingting.framework.dingtalk

import live.lingting.framework.dingtalk.message.DingTalkMessage
import live.lingting.framework.queue.WaitQueue
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * 订单负载均衡消息发送
 *
 * @author lingting 2020/6/10 21:25
 */
class DingTalkBalancedSender {
    private val queue = WaitQueue<DingTalkSender>()

    fun add(vararg senders: DingTalkSender): DingTalkBalancedSender {
        for (sender in senders) {
            queue.add(sender)
        }
        return this
    }

    fun addAll(collection: Collection<DingTalkSender>): DingTalkBalancedSender {
        queue.addAll(collection)
        return this
    }


    protected fun sender(): DingTalkSender {
        return queue.poll()
    }

    fun send(message: DingTalkMessage): DingTalkResponse {
        val sender = sender()
        try {
            return sender.sendMessage(message)
        } finally {
            queue.add(sender)
        }
    }

    fun retry(message: DingTalkMessage) {
        while (true) {
            try {
                val response = send(message)
                if (response.isSuccess) {
                    return
                }
                log.error("钉钉消息发送失败! code: {}; message: {}", response.code, response.message)
            } catch (e: Exception) {
                log.error("钉钉消息发送异常!", e)
            }
        }
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(DingTalkBalancedSender::class.java)
    }
}
