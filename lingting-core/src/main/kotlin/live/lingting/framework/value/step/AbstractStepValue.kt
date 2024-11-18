package live.lingting.framework.value.step

import java.math.BigInteger
import live.lingting.framework.value.StepValue

/**
 * @author lingting 2024-02-27 14:11
 */
abstract class AbstractStepValue<T> : StepValue<T> {
    protected var index: BigInteger = DEFAULT_INDEX

    protected var values: List<T>? = null

    override fun index(): BigInteger {
        return index
    }

    override fun reset() {
        index = DEFAULT_INDEX
    }

    override fun values(): List<T> {
        if (values != null) {
            return values!!
        }
        val list: MutableList<T> = ArrayList()
        val copy = copy()
        while (copy.hasNext()) {
            list.add(copy.next())
        }
        values = list
        return list
    }

    override fun next(): T {
        if (!hasNext()) {
            throw NoSuchElementException()
        }
        val increasing = increasing()
        if (increasing.compareTo(BigInteger.ZERO) == 0) {
            return first()
        }
        return calculate(increasing)
    }

    open fun increasing(): BigInteger {
        val current = index.add(BigInteger.ONE)
        index = current
        return current
    }

    open fun calculateNext(): T {
        return calculate(index.add(BigInteger.ONE))
    }

    abstract fun calculate(index: BigInteger): T

    companion object {
        @JvmField
        val DEFAULT_INDEX: BigInteger = BigInteger.valueOf(0)
    }
}
