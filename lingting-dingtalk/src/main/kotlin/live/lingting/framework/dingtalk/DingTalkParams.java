package live.lingting.framework.dingtalk;

import com.fasterxml.jackson.annotation.JsonProperty;
import live.lingting.framework.dingtalk.message.DingTalkActionCardMessage;
import live.lingting.framework.jackson.JacksonUtils;

import java.util.List;
import java.util.Set;

/**
 * @author lingting 2020/6/12 19:35
 */

public class DingTalkParams {

	@JsonProperty("msgtype")
	private String type;

	private At at;

	private ActionCard actionCard;

	private Link link;

	private Markdown markdown;

	private Text text;

	public String json() {
		return JacksonUtils.toJson(this);
	}

	public String getType() {return this.type;}

	public At getAt() {return this.at;}

	public ActionCard getActionCard() {return this.actionCard;}

	public Link getLink() {return this.link;}

	public Markdown getMarkdown() {return this.markdown;}

	public Text getText() {return this.text;}

	@JsonProperty("msgtype")
	public DingTalkParams setType(String type) {
		this.type = type;
		return this;
	}

	public DingTalkParams setAt(At at) {
		this.at = at;
		return this;
	}

	public DingTalkParams setActionCard(ActionCard actionCard) {
		this.actionCard = actionCard;
		return this;
	}

	public DingTalkParams setLink(Link link) {
		this.link = link;
		return this;
	}

	public DingTalkParams setMarkdown(Markdown markdown) {
		this.markdown = markdown;
		return this;
	}

	public DingTalkParams setText(Text text) {
		this.text = text;
		return this;
	}


	public static class Text {

		private String content;

		public String getContent() {return this.content;}

		public Text setContent(String content) {
			this.content = content;
			return this;
		}
	}


	public static class Markdown {

		private String title;

		private String text;

		public String getTitle() {return this.title;}

		public String getText() {return this.text;}

		public Markdown setTitle(String title) {
			this.title = title;
			return this;
		}

		public Markdown setText(String text) {
			this.text = text;
			return this;
		}
	}


	public static class Link {

		private String text;

		private String title;

		private String picUrl;

		private String messageUrl;

		public String getText() {return this.text;}

		public String getTitle() {return this.title;}

		public String getPicUrl() {return this.picUrl;}

		public String getMessageUrl() {return this.messageUrl;}

		public Link setText(String text) {
			this.text = text;
			return this;
		}

		public Link setTitle(String title) {
			this.title = title;
			return this;
		}

		public Link setPicUrl(String picUrl) {
			this.picUrl = picUrl;
			return this;
		}

		public Link setMessageUrl(String messageUrl) {
			this.messageUrl = messageUrl;
			return this;
		}
	}


	public static class ActionCard {

		private String title;

		private String text;

		private String btnOrientation;

		private String singleTitle;

		@JsonProperty("singleURL")
		private String singleUrl;

		@JsonProperty("btns")
		private List<DingTalkActionCardMessage.Button> buttons;

		public String getTitle() {return this.title;}

		public String getText() {return this.text;}

		public String getBtnOrientation() {return this.btnOrientation;}

		public String getSingleTitle() {return this.singleTitle;}

		public String getSingleUrl() {return this.singleUrl;}

		public List<DingTalkActionCardMessage.Button> getButtons() {return this.buttons;}

		public ActionCard setTitle(String title) {
			this.title = title;
			return this;
		}

		public ActionCard setText(String text) {
			this.text = text;
			return this;
		}

		public ActionCard setBtnOrientation(String btnOrientation) {
			this.btnOrientation = btnOrientation;
			return this;
		}

		public ActionCard setSingleTitle(String singleTitle) {
			this.singleTitle = singleTitle;
			return this;
		}

		@JsonProperty("singleURL")
		public ActionCard setSingleUrl(String singleUrl) {
			this.singleUrl = singleUrl;
			return this;
		}

		@JsonProperty("btns")
		public ActionCard setButtons(List<DingTalkActionCardMessage.Button> buttons) {
			this.buttons = buttons;
			return this;
		}
	}


	public static class At {

		@JsonProperty("isAtAll")
		private boolean atAll;

		private Set<String> atMobiles;

		public boolean isAtAll() {return this.atAll;}

		public Set<String> getAtMobiles() {return this.atMobiles;}

		@JsonProperty("isAtAll")
		public At setAtAll(boolean atAll) {
			this.atAll = atAll;
			return this;
		}

		public At setAtMobiles(Set<String> atMobiles) {
			this.atMobiles = atMobiles;
			return this;
		}
	}

}
