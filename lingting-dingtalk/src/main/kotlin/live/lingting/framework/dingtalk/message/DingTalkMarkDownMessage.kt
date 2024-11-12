package live.lingting.framework.dingtalk.message;

import live.lingting.framework.MarkdownBuilder;
import live.lingting.framework.dingtalk.DingTalkParams;
import live.lingting.framework.dingtalk.enums.MessageTypeEnum;

/**
 * @author lingting 2020/6/10 22:13
 */

public class DingTalkMarkDownMessage extends AbstractDingTalkMessage {

	/**
	 * 标题
	 */
	private String title;

	/**
	 * 内容
	 */
	private MarkdownBuilder text;

	@Override
	public MessageTypeEnum getType() {
		return MessageTypeEnum.MARKDOWN;
	}

	@Override
	public DingTalkParams put(DingTalkParams params) {
		return params.setMarkdown(new DingTalkParams.Markdown().setTitle(title).setText(text.build()));
	}

	public String getTitle() {return this.title;}

	public MarkdownBuilder getText() {return this.text;}

	public DingTalkMarkDownMessage setTitle(String title) {
		this.title = title;
		return this;
	}

	public DingTalkMarkDownMessage setText(MarkdownBuilder text) {
		this.text = text;
		return this;
	}
}
