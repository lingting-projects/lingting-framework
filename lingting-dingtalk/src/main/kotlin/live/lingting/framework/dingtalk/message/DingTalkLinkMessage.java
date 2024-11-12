package live.lingting.framework.dingtalk.message;

import live.lingting.framework.dingtalk.DingTalkParams;
import live.lingting.framework.dingtalk.enums.MessageTypeEnum;

/**
 * @author lingting 2020/6/10 22:13
 */

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

	public String getText() {return this.text;}

	public String getTitle() {return this.title;}

	public String getPicUrl() {return this.picUrl;}

	public String getMessageUrl() {return this.messageUrl;}

	public DingTalkLinkMessage setText(String text) {
		this.text = text;
		return this;
	}

	public DingTalkLinkMessage setTitle(String title) {
		this.title = title;
		return this;
	}

	public DingTalkLinkMessage setPicUrl(String picUrl) {
		this.picUrl = picUrl;
		return this;
	}

	public DingTalkLinkMessage setMessageUrl(String messageUrl) {
		this.messageUrl = messageUrl;
		return this;
	}
}
