package live.lingting.framework.value.step;

import live.lingting.framework.value.StepValue;

import java.math.BigInteger;

/**
 * @author lingting 2024-02-27 15:44
 */
public class LongStepValue extends AbstractConcurrentStepValue<Long> {

	/**
	 * 与初始值
	 */
	protected final Long start;

	/**
	 * 每次步进值
	 */
	protected final Long step;

	/**
	 * 最大步进次数, 为null表示无限步进次数
	 */
	protected final Long maxIndex;

	/**
	 * 最大值(可以等于), 为null表示无最大值限制
	 */
	protected final Long maxValue;

	public LongStepValue(long step, long maxIndex, long maxValue) {
		this(step, Long.valueOf(maxIndex), Long.valueOf(maxValue));
	}

	public LongStepValue(long step, Long maxIndex, Long maxValue) {
		this(0, step, maxIndex, maxValue);
	}

	public LongStepValue(long start, Long step, Long maxIndex, Long maxValue) {
		if (step == null) {
			throw new IllegalArgumentException(String.format("Invalid step value[%d].", step));
		}
		this.start = start;
		this.step = step;
		this.maxIndex = maxIndex;
		this.maxValue = maxValue;
	}

	public LongStepValue start(Long start) {
		return new LongStepValue(start, step, maxIndex, maxValue);
	}

	@Override
	public Long firt() {
		return start;
	}

	@Override
	public StepValue<Long> copy() {
		return start(start);
	}

	@Override
	public boolean doHasNext() {
		// 当前索引位置大于等于最大索引位置则不可以继续步进
		if (maxIndex != null && index.longValue() >= maxIndex) {
			return false;
		}
		// 下一个值大于最大值则不可以继续步进
		if (maxValue != null) {
			Long next = calculateNext();
			return next.compareTo(maxValue) < 1;
		}
		return true;
	}

	@Override
	public Long doCalculate(BigInteger index) {
		Long decimal = index.longValue();
		// 步进值 * 索引位置 = 增长值
		long growth = step * decimal;
		// 与初始值相加
		return start + growth;
	}

}
