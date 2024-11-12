package live.lingting.framework.dingtalk.enums;

/**
 * 跳转 ActionCard 类型 消息的按钮排列方式
 *
 * @author lingting 2020/6/10 23:44
 */
public enum ActionBtnOrientationEnum {

	/**
	 * 按钮排列样式值 说明
	 */
	VERTICAL("0", "按钮竖向排列"),

	HORIZONTAL("1", "按钮横向排列"),

	;

	private final String val;

	private final String text;

	private ActionBtnOrientationEnum(String val, String text) {
		this.val = val;
		this.text = text;
	}

	public String getVal() {return this.val;}

	public String getText() {return this.text;}
}
