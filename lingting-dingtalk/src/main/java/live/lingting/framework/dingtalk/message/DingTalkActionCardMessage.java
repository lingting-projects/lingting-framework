package live.lingting.framework.dingtalk.message;

import live.lingting.framework.MarkdownBuilder;
import live.lingting.framework.dingtalk.DingTalkParams;
import live.lingting.framework.dingtalk.enums.ActionBtnOrientationEnum;
import live.lingting.framework.dingtalk.enums.MessageTypeEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * 跳转 ActionCard类型
 *
 * @author lingting 2020/6/10 23:39
 */

public class DingTalkActionCardMessage extends AbstractDingTalkMessage {

	private String title;

	/**
	 * 内容
	 */
	private MarkdownBuilder text;

	/**
	 * 按钮排列样式 默认横
	 */
	private ActionBtnOrientationEnum orientation = ActionBtnOrientationEnum.HORIZONTAL;

	/**
	 * 单个按钮的标题
	 */
	private String singleTitle;

	/**
	 * 点击singleTitle按钮触发的URL
	 */
	private String singleUrl;

	/**
	 * 自定义按钮组 如果配置了 按钮组 则 单按钮配置无效
	 */
	private List<Button> buttons = new ArrayList<>();

	/**
	 * 添加按钮
	 */
	public DingTalkActionCardMessage addButton(String title, String url) {
		buttons.add(new Button(title, url));
		return this;
	}

	@Override
	public MessageTypeEnum getType() {
		return MessageTypeEnum.ACTION_CARD;
	}

	@Override
	public DingTalkParams put(DingTalkParams params) {
		DingTalkParams.ActionCard card = new DingTalkParams.ActionCard().setTitle(title)
			.setText(text.build())
			.setBtnOrientation(orientation.getVal());

		// 当 单按钮的 文本和链接都不为空时
		if (buttons.isEmpty()) {
			card.setSingleTitle(singleTitle).setSingleUrl(singleUrl);
		}
		else {
			card.setButtons(buttons);
		}
		return params.setActionCard(card);
	}

	public String getTitle() {return this.title;}

	public MarkdownBuilder getText() {return this.text;}

	public ActionBtnOrientationEnum getOrientation() {return this.orientation;}

	public String getSingleTitle() {return this.singleTitle;}

	public String getSingleUrl() {return this.singleUrl;}

	public List<Button> getButtons() {return this.buttons;}

	public DingTalkActionCardMessage setTitle(String title) {
		this.title = title;
		return this;
	}

	public DingTalkActionCardMessage setText(MarkdownBuilder text) {
		this.text = text;
		return this;
	}

	public DingTalkActionCardMessage setOrientation(ActionBtnOrientationEnum orientation) {
		this.orientation = orientation;
		return this;
	}

	public DingTalkActionCardMessage setSingleTitle(String singleTitle) {
		this.singleTitle = singleTitle;
		return this;
	}

	public DingTalkActionCardMessage setSingleUrl(String singleUrl) {
		this.singleUrl = singleUrl;
		return this;
	}

	public DingTalkActionCardMessage setButtons(List<Button> buttons) {
		this.buttons = buttons;
		return this;
	}

	public static class Button {

		/**
		 * 标题
		 */
		private final String title;

		/**
		 * 跳转路径
		 */
		private final String actionURL;

		public Button(String title, String actionURL) {
			this.title = title;
			this.actionURL = actionURL;
		}

		public String getTitle() {return this.title;}

		public String getActionURL() {return this.actionURL;}
	}

}
