package live.lingting.framework.dingtalk;

import com.fasterxml.jackson.annotation.JsonProperty;
import live.lingting.framework.dingtalk.message.DingTalkActionCardMessage;
import live.lingting.framework.jackson.JacksonUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Set;

/**
 * @author lingting 2020/6/12 19:35
 */
@Getter
@Setter
@Accessors(chain = true)
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

	@Getter
	@Setter
	@Accessors(chain = true)
	public static class Text {

		private String content;

	}

	@Getter
	@Setter
	@Accessors(chain = true)
	public static class Markdown {

		private String title;

		private String text;

	}

	@Getter
	@Setter
	@Accessors(chain = true)
	public static class Link {

		private String text;

		private String title;

		private String picUrl;

		private String messageUrl;

	}

	@Getter
	@Setter
	@Accessors(chain = true)
	public static class ActionCard {

		private String title;

		private String text;

		private String btnOrientation;

		private String singleTitle;

		@JsonProperty("singleURL")
		private String singleUrl;

		@JsonProperty("btns")
		private List<DingTalkActionCardMessage.Button> buttons;

	}

	@Getter
	@Setter
	@Accessors(chain = true)
	public static class At {

		@JsonProperty("isAtAll")
		private boolean atAll;

		private Set<String> atMobiles;

	}

}
