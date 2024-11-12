package live.lingting.framework.dingtalk.enums;

/**
 * 钉钉消息类型
 *
 * @author lingting 2020/6/10 21:29
 */
public enum MessageTypeEnum {

	/**
	 * 消息值 消息说明
	 */
	TEXT("text", "文本"),

	LINK("link", "链接"),

	MARKDOWN("markdown", "markdown"),

	ACTION_CARD("actionCard", "跳转 actionCard 类型"),

	;

	private final String val;

	private final String desc;

	private MessageTypeEnum(String val, String desc) {
		this.val = val;
		this.desc = desc;
	}

	public String getVal() {return this.val;}

	public String getDesc() {return this.desc;}
}
