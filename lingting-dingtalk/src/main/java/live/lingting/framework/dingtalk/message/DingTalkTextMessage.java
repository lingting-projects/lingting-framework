package live.lingting.framework.dingtalk.message;

import live.lingting.framework.dingtalk.DingTalkParams;
import live.lingting.framework.dingtalk.enums.MessageTypeEnum;

/**
 * @author lingting 2020/6/10 22:13
 */
public class DingTalkTextMessage extends AbstractDingTalkMessage {

	/**
	 * 消息内容
	 */
	private String content;

	@Override
	public MessageTypeEnum getType() {
		return MessageTypeEnum.TEXT;
	}

	@Override
	public DingTalkParams put(DingTalkParams params) {
		return params.setText(new DingTalkParams.Text().setContent(content));
	}

	public String getContent() {return this.content;}

	public DingTalkTextMessage setContent(String content) {
		this.content = content;
		return this;
	}
}
