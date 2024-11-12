package live.lingting.framework.value;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;

/**
 * @author lingting 2023-12-19 10:58
 */
public interface StepValue<T> extends Iterator<T> {

	T firt();

	/**
	 * 获取当前索引, 初始值为0
	 */
	BigInteger index();

	/**
	 * 重置索引为初始值
	 */
	void reset();

	/**
	 * 复制一份当前数据
	 */
	StepValue<T> copy();

	List<T> values();

	/**
	 * 返回下一个索引指向的元素
	 */
	@Override
	T next();

}
