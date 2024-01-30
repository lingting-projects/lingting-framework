package live.lingting.framework.dingtalk.message;

import live.lingting.framework.dingtalk.DingTalkParams;
import live.lingting.framework.dingtalk.MarkdownBuilder;
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

}
