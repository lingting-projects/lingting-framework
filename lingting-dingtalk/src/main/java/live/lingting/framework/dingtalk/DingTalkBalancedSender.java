package live.lingting.framework.dingtalk;

import live.lingting.framework.dingtalk.message.DingTalkMessage;
import live.lingting.framework.queue.WaitQueue;
import lombok.SneakyThrows;

import java.util.Collection;

/**
 * 订单负载均衡消息发送
 *
 * @author lingting 2020/6/10 21:25
 */
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

}
