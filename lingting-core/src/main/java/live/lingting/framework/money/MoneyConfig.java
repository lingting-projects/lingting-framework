package live.lingting.framework.money;

import live.lingting.framework.exception.BizException;

import java.math.RoundingMode;

/**
 * @author lingting 2023-05-07 17:55
 */
@SuppressWarnings("java:S6548")
public class MoneyConfig {

	/**
	 * 默认金额配置
	 */
	public static final MoneyConfig DEFAULT = new MoneyConfig(2, RoundingMode.HALF_UP, null, "");

	/**
	 * 小数位数量, 如果值非大于0则舍弃小数
	 */
	final Integer decimalLimit;

	/**
	 * 小数位处理方案, 当小数位数量值有效时使用, 如果值为空则使用 {@link Money#DEFAULT_DECIMAL_TYPE}
	 */
	final RoundingMode decimalType;

	/**
	 * 分位间隔数量, 如果值非大于0则不进行分位处理
	 * <p>
	 * 2: 百分位
	 * </p>
	 * <p>
	 * 3: 千分位
	 * </p>
	 * <p>
	 * 4: 万分位
	 * </p>
	 */
	final Integer quantileLimit;

	/**
	 * 分位符号
	 */
	final String quantileSymbol;

	public MoneyConfig(Integer decimalLimit, RoundingMode decimalType, Integer quantileLimit, String quantileSymbol) {
		this.decimalLimit = decimalLimit;
		this.decimalType = decimalType;
		this.quantileLimit = quantileLimit;
		this.quantileSymbol = quantileSymbol;
	}

	/**
	 * 小数位数量是否有效
	 *
	 * @return true 有效, 需要进行小数位处理
	 */
	public static boolean validDecimal(Integer decimalLimit) {
		return decimalLimit != null && decimalLimit > 0;
	}

	/**
	 * 分位配置是否有效
	 *
	 * @return true 表示有效, 需要进行分位控制
	 */
	public static boolean validQuantile(Integer quantileLimit) {
		return quantileLimit != null && quantileLimit > 0;
	}

	/**
	 * 校验分位配置
	 */
	public static void validQuantile(Integer quantileLimit, String quantileSymbol) {
		// 无效分位配置不管
		if (!validQuantile(quantileLimit)) {
			return;
		}
		// 有效的分位配置 , 分位符号必须正常
		if (quantileSymbol == null || quantileSymbol.isEmpty()) {
			throw new BizException(MoneyResultCode.CONFIG_ERROR);
		}
	}

}
