package live.lingting.framework.value.cycle;

/**
 * @author lingting 2024-01-23 15:22
 */
public interface CycleValueFunction<T> {

	/**
	 * 是否存在下一个元素, 如果返回false, 则会调用 {@link CycleValueFunction#reset()}
	 * @return true 存在下一个元素
	 */
	boolean hasNext();

	/**
	 * 获取下一个元素
	 * @return 下一个
	 */
	T next();

	/**
	 * 重置元素, 重新从第一个开始
	 */
	void reset();

}
