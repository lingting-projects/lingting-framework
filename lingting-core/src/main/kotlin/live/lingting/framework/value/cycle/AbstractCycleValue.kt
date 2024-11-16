package live.lingting.framework.value.cycle

import java.math.BigInteger
import live.lingting.framework.value.CycleValue

/**
 * @author lingting 2024-02-27 19:19
 */
abstract class AbstractCycleValue<T> : CycleValue<T> {
    protected var count: BigInteger = BigInteger.ZERO

    override fun count(): BigInteger {
        return count
    }

    override fun next(): T {
        count = count.add(BigInteger.ONE)
        return doNext()
    }

    abstract fun doNext(): T
}
