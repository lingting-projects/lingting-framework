package live.lingting.framework.value.step;

import live.lingting.framework.value.StepValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 简单步进器, 第一个值为 startValue + step
 */
@Getter
public class SimpleStepValue extends StepValue<Long> {

	/**
	 * 每次步进值
	 */
	protected final long step;

	/**
	 * 最大步进次数, 为null表示无限步进次数
	 */
	protected final Long maxStepCount;

	/**
	 * 最大步进值, 为null表示无最大值限制
	 */
	protected final Long maxStepValue;

	public SimpleStepValue(long step, Long maxStepCount, Long maxStepValue) {
		super(0L, new Function(step, maxStepCount, maxStepValue));
		this.step = step;
		this.maxStepCount = maxStepCount;
		this.maxStepValue = maxStepValue;
	}

	/**
	 * @author lingting 2024-01-15 19:22
	 */
	@Getter
	@RequiredArgsConstructor
	public static class Function implements StepFunction<Long> {

		/**
		 * 每次步进值
		 */
		protected final long step;

		/**
		 * 最大步进次数, 为null表示无限步进次数
		 */
		protected final Long maxStepCount;

		/**
		 * 最大步进值, 为null表示无最大值限制
		 */
		protected final Long maxStepValue;

		@Override
		public Long next(long count, Long previous) {
			// 超次数
			if (maxStepCount != null && count >= maxStepCount) {
				return null;
			}
			// 上一个值超限
			if (maxStepValue != null && previous != null && previous > maxStepValue) {
				return null;
			}
			// 获取下一个值
			long next = previous == null ? step : previous + step;
			// 下一个值超限
			if (maxStepValue != null && next > maxStepValue) {
				return null;
			}
			return next;
		}

	}

}
