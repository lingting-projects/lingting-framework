package live.lingting.framework.value.step

import java.math.BigInteger
import live.lingting.framework.lock.JavaReentrantLock

/**
 * @author lingting 2024-01-15 19:21
 */
abstract class AbstractConcurrentStepValue<T> : AbstractStepValue<T>() {
    protected val lock: JavaReentrantLock = JavaReentrantLock()

    override fun first(): T {
        return calculate(BigInteger.ZERO)
    }

    override fun index(): BigInteger {
        return lock.getByInterruptibly { super.index() }
    }

    override fun reset() {
        lock.runByInterruptibly { super.reset() }
    }

    override fun values(): List<T> {
        return lock.getByInterruptibly<List<T>> { super.values() }
    }

    override fun next(): T {
        return lock.getByInterruptibly {
            if (!hasNext()) {
                throw NoSuchElementException()
            }
            doNext()
        }
    }

    override fun increasing(): BigInteger {
        return lock.getByInterruptibly { super.increasing() }
    }

    override fun calculateNext(): T {
        return lock.getByInterruptibly { super.calculateNext() }
    }

    override fun calculate(index: BigInteger): T {
        return lock.getByInterruptibly { doCalculate(index) }
    }

    override fun hasNext(): Boolean {
        return lock.getByInterruptibly { this.doHasNext() }
    }

    abstract fun doHasNext(): Boolean

    open fun doNext(): T {
        return super.next()
    }

    abstract fun doCalculate(index: BigInteger): T
}
