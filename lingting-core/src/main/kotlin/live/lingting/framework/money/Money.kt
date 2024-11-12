package live.lingting.framework.money;

import live.lingting.framework.exception.BizException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * @author lingting 2023-05-07 17:44
 */
public class Money {

	public static final RoundingMode DEFAULT_DECIMAL_TYPE = RoundingMode.HALF_UP;

	public static final Money ZERO = Money.of(0);

	public static final Money TEN = Money.of(10);

	public static final Money HUNDRED = Money.of(100);

	/**
	 * 小数位数量, 如果值非大于0则舍弃小数
	 */
	private final Integer decimalLimit;

	/**
	 * 小数位处理方案, 当小数位数量值有效时使用, 如果值为空则使用 {@link Money#DEFAULT_DECIMAL_TYPE}
	 */
	private final RoundingMode decimalType;

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
	private final Integer quantileLimit;

	/**
	 * 分位符号
	 */
	private final String quantileSymbol;

	/**
	 * 值
	 */
	private BigDecimal value;

	private Money(Integer decimalLimit, RoundingMode decimalType, Integer quantileLimit, String quantileSymbol, BigDecimal value) {
		this.decimalLimit = decimalLimit;
		this.decimalType = decimalType;
		this.quantileLimit = quantileLimit;
		this.quantileSymbol = quantileSymbol;
		this.value = value;
	}

	/**
	 * 通过指定金额值和上下文的配置进行构建
	 *
	 * @param value 金额值
	 * @return 金额实例
	 */
	public static Money of(String value) {
		return of(new BigDecimal(value));
	}

	/**
	 * 通过指定金额值和上下文的配置进行构建
	 *
	 * @param value 金额值
	 * @return 金额实例
	 */
	public static Money of(long value) {
		return of(BigDecimal.valueOf(value));
	}

	/**
	 * 通过指定金额值和上下文的配置进行构建
	 *
	 * @param value 金额值
	 * @return 金额实例
	 */
	public static Money of(double value) {
		return of(BigDecimal.valueOf(value));
	}

	/**
	 * 通过指定金额值和上下文的配置进行构建
	 *
	 * @param value 金额值
	 * @return 金额实例
	 */
	public static Money of(BigDecimal value) {
		// 上下文自定义的金额配置
		MoneyConfig config = MoneyConfigHolder.get();
		if (config != null) {
			return of(value, config);
		}
		return of(value, MoneyConfig.DEFAULT);
	}

	/**
	 * 通过指定金额值和金额配置进行校验
	 *
	 * @param value  金额值
	 * @param config 金额配置
	 * @return 金额实例
	 */
	public static Money of(BigDecimal value, MoneyConfig config) {
		return of(value, config.decimalLimit, config.decimalType, config.quantileLimit, config.quantileSymbol);
	}

	/**
	 * 通过金额值和具体配置进行构建
	 * <p>
	 * 会进行参数校验
	 * </p>
	 *
	 * @param value         金额值
	 * @param decimalLimit  小数位限制数量
	 * @param decimalType   小数位处理方案
	 * @param quantileLimit 分位间隔数量
	 * @return 金额实例
	 */
	public static Money of(BigDecimal value, Integer decimalLimit, RoundingMode decimalType, Integer quantileLimit,
						   String quantileSymbol) {
		if (value == null) {
			throw new BizException(MoneyResultCode.VALUE_ERROR);
		}
		// 小数位配置, 如果指定了小数位数量, 但是未指定小数位处理方案, 则使用默认的方案
		if (MoneyConfig.validDecimal(decimalLimit)) {
			decimalType = decimalType == null ? DEFAULT_DECIMAL_TYPE : decimalType;
		}
		// 如果未指定小数位数量则舍弃小数
		else {
			decimalType = RoundingMode.DOWN;
		}
		// 分位配置
		if (MoneyConfig.validQuantile(quantileLimit)) {
			// 有效分位则进行校验
			MoneyConfig.validQuantile(quantileLimit, quantileSymbol);
		}
		return ofPrivate(value, decimalLimit, decimalType, quantileLimit, quantileSymbol);
	}

	/**
	 * 内部用 - 通过金额值和具体配置进行构建
	 * <p>
	 * 不进行参数校验
	 * </p>
	 *
	 * @param value         金额值
	 * @param decimalLimit  小数位限制数量
	 * @param decimalType   小数位处理方案
	 * @param quantileLimit 分位间隔数量
	 * @return 金额实例
	 */
	private static Money ofPrivate(BigDecimal value, Integer decimalLimit, RoundingMode decimalType,
								   Integer quantileLimit, String quantileSymbol) {
		return new Money(decimalLimit, decimalType, quantileLimit, quantileSymbol,
			value.setScale(decimalLimit, decimalType));
	}

	// region 金额变动操作

	/**
	 * 切换金额
	 *
	 * @param value 值
	 * @return this
	 */
	public Money use(BigDecimal value) {
		return ofPrivate(value, decimalLimit, decimalType, quantileLimit, quantileSymbol);
	}

	/**
	 * 增加
	 *
	 * @param money 金额
	 * @return 增加指定金额后的新金额
	 */
	public Money add(Money money) {
		return add(money.getValue());
	}

	/**
	 * 增加
	 *
	 * @param money 金额
	 * @return 增加指定金额后的新金额
	 */
	public Money add(long money) {
		return add(BigDecimal.valueOf(money));
	}

	/**
	 * 增加
	 *
	 * @param money 金额
	 * @return 增加指定金额后的新金额
	 */
	public Money add(double money) {
		return add(BigDecimal.valueOf(money));
	}

	/**
	 * 增加
	 *
	 * @param money 金额
	 * @return 增加指定金额后的新金额
	 */
	public Money add(BigDecimal money) {
		return use(getValue().add(money));
	}

	/**
	 * 减少
	 *
	 * @param money 金额
	 * @return 减少指定金额后的新金额
	 */
	public Money subtract(Money money) {
		return subtract(money.getValue());
	}

	/**
	 * 减少
	 *
	 * @param money 金额
	 * @return 减少指定金额后的新金额
	 */
	public Money subtract(long money) {
		return subtract(BigDecimal.valueOf(money));
	}

	/**
	 * 减少
	 *
	 * @param money 金额
	 * @return 减少指定金额后的新金额
	 */
	public Money subtract(double money) {
		return subtract(BigDecimal.valueOf(money));
	}

	/**
	 * 减少
	 *
	 * @param money 金额
	 * @return 减少指定金额后的新金额
	 */
	public Money subtract(BigDecimal money) {
		return use(getValue().subtract(money));
	}

	/**
	 * 乘以
	 *
	 * @param money 金额
	 * @return 乘以指定金额后的新金额
	 */
	public Money multiply(Money money) {
		return multiply(money.getValue());
	}

	/**
	 * 乘以
	 *
	 * @param money 金额
	 * @return 乘以指定金额后的新金额
	 */
	public Money multiply(long money) {
		return multiply(BigDecimal.valueOf(money));
	}

	/**
	 * 乘以
	 *
	 * @param money 金额
	 * @return 乘以指定金额后的新金额
	 */
	public Money multiply(double money) {
		return multiply(BigDecimal.valueOf(money));
	}

	/**
	 * 乘以
	 *
	 * @param money 金额
	 * @return 乘以指定金额后的新金额
	 */
	public Money multiply(BigDecimal money) {
		return use(getValue().multiply(money));
	}

	/**
	 * 除以
	 *
	 * @param money 金额
	 * @return 除以指定金额后的新金额
	 */
	public Money divide(Money money) {
		return divide(money.getValue());
	}

	/**
	 * 除以
	 *
	 * @param money 金额
	 * @return 除以指定金额后的新金额
	 */
	public Money divide(long money) {
		return divide(BigDecimal.valueOf(money));
	}

	/**
	 * 除以
	 *
	 * @param money 金额
	 * @return 除以指定金额后的新金额
	 */
	public Money divide(double money) {
		return divide(BigDecimal.valueOf(money));
	}

	/**
	 * 除以
	 *
	 * @param money 金额
	 * @return 除以指定金额后的新金额
	 */
	public Money divide(BigDecimal money) {
		return use(getValue().divide(money, decimalType));
	}

	/**
	 * 取反
	 */
	public Money negate() {
		return use(getValue().negate());
	}

	// endregion

	// region 金额比对操作

	/**
	 * 是否为0
	 *
	 * @return boolean
	 */
	public boolean isZero() {
		return getValue().compareTo(BigDecimal.ZERO) == 0;
	}

	/**
	 * 是否为负数
	 *
	 * @return boolean
	 */
	public boolean isNegative() {
		return getValue().compareTo(BigDecimal.ZERO) < 0;
	}

	/**
	 * 大于
	 *
	 * @param money 金额
	 * @return boolean true 表示大于目标金额
	 */
	public boolean isGt(Money money) {
		return isGt(money.getValue());
	}

	/**
	 * 大于
	 *
	 * @param money 金额
	 * @return boolean true 表示大于目标金额
	 */
	public boolean isGt(BigDecimal money) {
		return getValue().compareTo(money) > 0;
	}

	/**
	 * 大于等于
	 *
	 * @param money 金额
	 * @return boolean true 表示大于等于目标金额
	 */
	public boolean isGe(Money money) {
		return isGe(money.getValue());
	}

	/**
	 * 大于等于
	 *
	 * @param money 金额
	 * @return boolean true 表示大于等于目标金额
	 */
	public boolean isGe(BigDecimal money) {
		return getValue().compareTo(money) > -1;
	}

	/**
	 * 等于
	 *
	 * @param money 金额
	 * @return boolean true 表示等于目标金额
	 */
	public boolean isEquals(Money money) {
		return isEquals(money.getValue());
	}

	/**
	 * 等于
	 *
	 * @param money 金额
	 * @return boolean true 表示等于目标金额
	 */
	public boolean isEquals(BigDecimal money) {
		return getValue().compareTo(money) == 0;
	}

	/**
	 * 小于
	 *
	 * @param money 金额
	 * @return boolean true 表示小于目标金额
	 */
	public boolean isLt(Money money) {
		return isLt(money.getValue());
	}

	/**
	 * 小于
	 *
	 * @param money 金额
	 * @return boolean true 表示小于目标金额
	 */
	public boolean isLt(BigDecimal money) {
		return getValue().compareTo(money) < 0;
	}

	/**
	 * 小于等于
	 *
	 * @param money 金额
	 * @return boolean true 表示小于等于目标金额
	 */
	public boolean isLe(Money money) {
		return isLe(money.getValue());
	}

	/**
	 * 小于等于
	 *
	 * @param money 金额
	 * @return boolean true 表示小于等于目标金额
	 */
	public boolean isLe(BigDecimal money) {
		return getValue().compareTo(money) < 1;
	}

	// endregion

	// region 值处理

	/**
	 * 返回原始值, 剔除无用的小数位
	 */
	public String toRawString() {
		return getValue().stripTrailingZeros().toPlainString();
	}

	/**
	 * 转化为指定配置对应的值.
	 * <p>
	 * 存在分位配置则加入嵌入分位
	 * </p>
	 *
	 * @return java.lang.String 字符串值
	 */
	public String toPlainString() {
		// 不用处理小数位, 每次计算结果都会进行处理
		String plainString = toRawString();

		Integer limit = getQuantileLimit();
		// 分位配置有效时解析
		if (MoneyConfig.validQuantile(limit)) {
			String[] split = plainString.split("\\.");
			// 整数位
			String integer = split[0];

			StringBuilder builder = new StringBuilder();
			int index = 1;
			// 倒序处理整数位
			for (int i = integer.length() - 1; i >= 0; i--) {
				// 放入到第一位
				builder.insert(0, integer.charAt(i));
				// 需要插入分位符号(必须不是最后一位)
				if (index % limit == 0 && i != 0) {
					builder.insert(0, quantileSymbol);
				}
				index++;
			}

			// 存在小数位则追加
			if (split.length > 1) {
				builder.append(".").append(split[1]);
			}
			return builder.toString();
		}

		return plainString;
	}

	/**
	 * 返回数据的值
	 *
	 * @return java.lang.String
	 */
	@Override
	public String toString() {
		return toPlainString();
	}

	// endregion

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Money money = (Money) o;
		return Objects.equals(value, money.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}

	public Integer getDecimalLimit() {return this.decimalLimit;}

	public RoundingMode getDecimalType() {return this.decimalType;}

	public Integer getQuantileLimit() {return this.quantileLimit;}

	public String getQuantileSymbol() {return this.quantileSymbol;}

	public BigDecimal getValue() {return this.value;}
}
