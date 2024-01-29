package live.lingting.framework.sensitive;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author lingting 2023-04-27 15:15
 */
@Getter
@AllArgsConstructor
public enum SensitiveType {

	/**
	 * 默认脱敏
	 * <p>
	 * 这是一个要脱敏的文本
	 * </p>
	 * <p>
	 * 这*****本
	 * </p>
	 */
	DEFAULT,
	/**
	 * 全脱敏
	 * <p>
	 * 这是一个要脱敏的文本
	 * </p>
	 * <p>
	 * *****
	 * </p>
	 */
	ALL,
	/**
	 * 手机号格式脱敏
	 * <p>
	 * +8617612349876
	 * </p>
	 * <p>
	 * +86*****76
	 * </p>
	 */
	MOBILE,
	/**
	 * 自定义脱敏. 使用 provider查询 serializer然后进行操作
	 */
	CUSTOMER,

	;

}
