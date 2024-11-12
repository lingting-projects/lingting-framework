package live.lingting.framework.value;

import java.math.BigInteger;

/**
 * 循环数据
 *
 * @author lingting 2023-12-19 16:01
 */
public interface CycleValue<T> {

	/**
	 * 已获取数据次数
	 */
	BigInteger count();

	/**
	 * 重置, 下一个数据为第一条数据
	 */
	void reset();

	/**
	 * 获取下一个数据
	 */
	T next();

}
