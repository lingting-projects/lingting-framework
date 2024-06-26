package live.lingting.framework.dingtalk.message;

import live.lingting.framework.dingtalk.DingTalkParams;
import live.lingting.framework.dingtalk.enums.MessageTypeEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author lingting 2020/6/10 22:13
 */
@Getter
@Setter
@Accessors(chain = true)
public class DingTalkLinkMessage extends AbstractDingTalkMessage {

	/**
	 * 文本
	 */
	private String text;

	/**
	 * 标题
	 */
	private String title;

	/**
	 * 图片url
	 */
	private String picUrl;

	/**
	 * 消息链接
	 */
	private String messageUrl;

	@Override
	public MessageTypeEnum getType() {
		return MessageTypeEnum.LINK;
	}

	@Override
	public DingTalkParams put(DingTalkParams params) {
		return params.setLink(
				new DingTalkParams.Link().setText(text).setTitle(title).setPicUrl(picUrl).setMessageUrl(messageUrl));
	}

}
