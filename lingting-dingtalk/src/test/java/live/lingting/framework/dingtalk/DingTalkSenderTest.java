package live.lingting.framework.dingtalk;

import live.lingting.framework.dingtalk.message.DingTalkTextMessage;
import live.lingting.framework.util.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * <p>
 * idea: 设置 build -> build -> gradle -> run test 为 idea. 然后配置在 vm options 里面
 * </p>
 *
 * @author lingting 2024-01-29 20:12
 */
@EnabledIfSystemProperty(named = "framework.dingtalk.test", matches = "true")
class DingTalkSenderTest {

	String webhook = System.getProperty("framework.dingtalk.webhook");

	String secret = System.getProperty("framework.dingtalk.secret");

	DingTalkSender sender;

	@BeforeEach
	void before() {
		sender = new DingTalkSender(webhook).setSecret(secret);
	}

	@Test
	void send() {
		assertTrue(StringUtils.hasText(sender.getUrl()));
		assertTrue(StringUtils.hasText(sender.getSecret()));
		DingTalkTextMessage message = new DingTalkTextMessage();
		message.setContent("测试机器人消息通知");
		DingTalkResponse response = sender.sendMessage(message);
		assertTrue(response.isSuccess());
	}

}
