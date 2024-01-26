package live.lingting.framework.value.step;

/**
 * @author lingting 2024-01-15 19:22
 */
@FunctionalInterface
public interface StepFunction<T> {

	/**
	 * 获取下一个值
	 * @param count 当前已获取值的次数, 初始值为0
	 * @param previous 上一个获取的值, 初始值为 null
	 * @return 返回下一个获取的值, 为null表示结束
	 */
	T next(long count, T previous);

}
