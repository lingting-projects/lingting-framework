package live.lingting.framework.value.step

import java.math.BigInteger
import java.util.concurrent.ConcurrentHashMap
import live.lingting.framework.value.StepValue

/**
 * @author lingting 2024-01-23 15:30
 */
open class IteratorStepValue<T> protected constructor(
    private val map: MutableMap<BigInteger, T>,
    private val iterator: Iterator<T>
) : AbstractConcurrentStepValue<T>() {

    constructor(iterator: Iterator<T>) : this(ConcurrentHashMap<BigInteger, T>(), iterator)

    override fun copy(): StepValue<T> {
        return IteratorStepValue(map, iterator)
    }

    override fun doHasNext(): Boolean {
        // 下一个已经取出来了, 为true
        if (map.containsKey(index.add(BigInteger.ONE))) {
            return true
        }
        // 没取出来, 用下一个值
        return iterator.hasNext()
    }

    override fun doNext(): T {
        return map.computeIfAbsent(increasing()) { i -> iterator.next() }
    }

    override fun doCalculate(index: BigInteger): T {
        if (map.containsKey(index)) {
            return map[index]!!
        }

        // 如果迭代器已经取空了
        if (!iterator.hasNext()) {
            throw NoSuchElementException()
        }

        // 没取空, 接着取
        val value = copy()
        while (value.hasNext()) {
            val t = value.next()
            if (index.compareTo(value.index()) == 0) {
                return t
            }
        }
        throw NoSuchElementException()
    }

    /**
     * 移除上一个next返回的元素
     */
    fun remove() {
        lock.runByInterruptibly {
            // 至少需要调用一次next
            check(index.compareTo(BigInteger.ZERO) != 0)
            // 被移除的索引
            val removeIndex = index
            // 移除索引位置的值
            map.remove(removeIndex)
            // 索引回调
            index = removeIndex.subtract(BigInteger.ONE)
            // values缓存移除
            values = null
            // 用于重设后续索引
            var current = removeIndex
            while (true) {
                // 下一个索引
                val next = current.add(BigInteger.ONE)
                // 从缓存中移除下一个
                val value = map.remove(next) ?: break
                // 不存在后续索引则结束
                // 存在值, 放入到上一个索引中
                map.put(current, value)
                // 步进, 用于处理下一个
                current = next
            }
        }
    }
}
