package live.lingting.framework.dingtalk

import live.lingting.framework.dingtalk.message.DingTalkTextMessage
import live.lingting.framework.util.StringUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfSystemProperty

/**
 *
 *
 * idea: 设置 build -> build -> gradle -> run test 为 idea. 然后配置在 vm options 里面
 *
 *
 * @author lingting 2024-01-29 20:12
 */
@EnabledIfSystemProperty(named = "framework.dingtalk.test", matches = "true")
internal class DingTalkSenderTest {
    var webhook: String = System.getProperty("framework.dingtalk.webhook")

    var secret: String = System.getProperty("framework.dingtalk.secret")

    var sender: DingTalkSender? = null

    @BeforeEach
    fun before() {
        sender = DingTalkSender(webhook).setSecret(secret)
    }

    @Test
    fun send() {
        assertTrue(StringUtils.hasText(sender!!.url))
        assertTrue(StringUtils.hasText(sender!!.secret))
        val message = DingTalkTextMessage()
        message.setContent("测试机器人消息通知")
        val response = sender!!.sendMessage(message)
        Assertions.assertTrue(response.isSuccess)
    }
}
