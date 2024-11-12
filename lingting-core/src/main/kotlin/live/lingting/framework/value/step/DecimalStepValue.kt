package live.lingting.framework.value.step;

import live.lingting.framework.value.StepValue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

/**
 * @author lingting 2024-02-27 14:15
 */
public class DecimalStepValue extends AbstractConcurrentStepValue<BigDecimal> {

	/**
	 * 与初始值
	 */
	protected final BigDecimal start;

	/**
	 * 每次步进值
	 */
	protected final BigDecimal step;

	/**
	 * 最大步进次数, 为null表示无限步进次数
	 */
	protected final BigInteger maxIndex;

	/**
	 * 最大值(可以等于), 为null表示无最大值限制
	 */
	protected final BigDecimal maxValue;

	public DecimalStepValue(BigDecimal step, BigInteger maxIndex, BigDecimal maxValue) {
		this(BigDecimal.ZERO, step, maxIndex, maxValue);
	}

	public DecimalStepValue(BigDecimal start, BigDecimal step, BigInteger maxIndex, BigDecimal maxValue) {
		if (start == null || step == null) {
			throw new IllegalArgumentException(
					String.format("Invalid start value[%s] or step value[%s].", start, step));
		}
		this.start = start;
		this.step = step;
		this.maxIndex = maxIndex;
		this.maxValue = maxValue;
	}

	public DecimalStepValue start(BigDecimal start) {
		return new DecimalStepValue(start, step, maxIndex, maxValue);
	}

	@Override
	public BigDecimal firt() {
		return start;
	}

	@Override
	public StepValue<BigDecimal> copy() {
		return start(start);
	}

	@Override
	public boolean doHasNext() {
		// 当前索引位置大于等于最大索引位置则不可以继续步进
		if (maxIndex != null && index.compareTo(maxIndex) > -1) {
			return false;
		}
		// 下一个值大于最大值则不可以继续步进
		if (maxValue != null) {
			BigDecimal next = calculateNext();
			return next.compareTo(maxValue) < 1;
		}
		return true;
	}

	@Override
	public BigDecimal doCalculate(BigInteger index) {
		BigDecimal decimal = new BigDecimal(index);
		// 步进值 * 索引位置 = 增长值
		BigDecimal growth = step.multiply(decimal, MathContext.UNLIMITED);
		// 与初始值相加
		return start.add(growth);
	}

}
