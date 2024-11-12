package live.lingting.framework.value.cycle

import live.lingting.framework.value.CycleValue
import java.math.BigInteger

/**
 * @author lingting 2024-02-27 19:19
 */
abstract class AbstractCycleValue<T> : CycleValue<T?> {
    protected var count: BigInteger = BigInteger.ZERO

    override fun count(): BigInteger {
        return count
    }

    override fun next(): T? {
        count = count.add(BigInteger.ONE)
        return doNext()
    }

    abstract fun doNext(): T?
}
