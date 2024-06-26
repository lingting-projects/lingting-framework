package live.lingting.framework.dingtalk;

import live.lingting.framework.dingtalk.message.DingTalkMessage;
import live.lingting.framework.queue.WaitQueue;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;

/**
 * 订单负载均衡消息发送
 *
 * @author lingting 2020/6/10 21:25
 */
@Slf4j
public class DingTalkBalancedSender {

	private final WaitQueue<DingTalkSender> queue = new WaitQueue<>();

	public DingTalkBalancedSender add(DingTalkSender... senders) {
		for (DingTalkSender sender : senders) {
			queue.add(sender);
		}
		return this;
	}

	public DingTalkBalancedSender addAll(Collection<DingTalkSender> collection) {
		queue.addAll(collection);
		return this;
	}

	@SneakyThrows
	protected DingTalkSender sender() {
		return queue.poll();
	}

	public DingTalkResponse send(DingTalkMessage message) {
		DingTalkSender sender = sender();
		try {
			return sender.sendMessage(message);
		}
		finally {
			queue.add(sender);
		}
	}

	public void retry(DingTalkMessage message) {
		while (true) {
			try {
				DingTalkResponse response = send(message);
				if (response.isSuccess()) {
					return;
				}
				log.error("钉钉消息发送失败! code: {}; message: {}", response.getCode(), response.getMessage());
			}
			catch (Exception e) {
				log.error("钉钉消息发送异常!", e);
			}
		}
	}

}
