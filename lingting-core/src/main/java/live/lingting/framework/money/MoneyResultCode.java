package live.lingting.framework.money;

import live.lingting.framework.api.ResultCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author lingting 2023-05-07 18:02
 */
@Getter
@AllArgsConstructor
public enum MoneyResultCode implements ResultCode {

	/**
	 * 金额值异常!
	 */
	VALUE_ERROR(2022010000, "金额值异常!"),
	/**
	 * 金额配置异常!
	 */
	CONFIG_ERROR(2022010001, "金额配置异常!"),

	;

	private final Integer code;

	private final String message;

}
