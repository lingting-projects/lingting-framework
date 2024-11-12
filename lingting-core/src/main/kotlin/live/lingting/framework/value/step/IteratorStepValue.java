package live.lingting.framework.value.step;

import live.lingting.framework.value.StepValue;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lingting 2024-01-23 15:30
 */
public class IteratorStepValue<T> extends AbstractConcurrentStepValue<T> {

	private final Map<BigInteger, T> map;

	private final Iterator<T> iterator;

	public IteratorStepValue(Iterator<T> iterator) {
		this(new ConcurrentHashMap<>(), iterator);
	}

	protected IteratorStepValue(Map<BigInteger, T> map, Iterator<T> iterator) {
		this.map = map;
		this.iterator = iterator;
	}

	@Override
	public StepValue<T> copy() {
		return new IteratorStepValue<>(map, iterator);
	}

	@Override
	public boolean doHasNext() {
		// 下一个已经取出来了, 为true
		if (map.containsKey(index.add(BigInteger.ONE))) {
			return true;
		}
		// 没取出来, 用下一个值
		return iterator.hasNext();
	}

	@Override
	public T doNext() {
		return map.computeIfAbsent(increasing(), i -> iterator.next());
	}

	@Override
	public T doCalculate(BigInteger index) {
		if (map.containsKey(index)) {
			return map.get(index);
		}

		// 如果迭代器已经取空了
		if (!iterator.hasNext()) {
			return null;
		}

		// 没取空, 接着取
		StepValue<T> value = copy();
		while (value.hasNext()) {
			T t = value.next();
			if (index.compareTo(value.index()) == 0) {
				return t;
			}
		}
		return null;
	}

	/**
	 * 移除上一个next返回的元素
	 */
	@Override

	public void remove() {
		lock.runByInterruptibly(() -> {
			// 至少需要调用一次next
			if (index.compareTo(BigInteger.ZERO) == 0) {
				throw new IllegalStateException();
			}
			// 被移除的索引
			BigInteger removeIndex = index;
			// 移除索引位置的值
			map.remove(removeIndex);
			// 索引回调
			index = removeIndex.subtract(BigInteger.ONE);
			// values缓存移除
			values = null;
			// 用于重设后续索引
			BigInteger current = removeIndex;
			while (true) {
				// 下一个索引
				BigInteger next = current.add(BigInteger.ONE);
				// 从缓存中移除下一个
				T value = map.remove(next);
				// 不存在后续索引则结束
				if (value == null) {
					break;
				}
				// 存在值, 放入到上一个索引中
				map.put(current, value);
				// 步进, 用于处理下一个
				current = next;
			}

		});
	}

}
