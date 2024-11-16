package live.lingting.framework.value.cycle

import live.lingting.framework.lock.JavaReentrantLock

/**
 * @author lingting 2024-02-27 19:19
 */
abstract class AbstractConcurrentCycleValue<T> : AbstractCycleValue<T>() {
    protected val lock: JavaReentrantLock = JavaReentrantLock()

    override fun reset() {
        lock.runByInterruptibly { this.doReset() }
    }

    override fun next(): T {
        return lock.getByInterruptibly { super.next() }
    }

    abstract fun doReset()

}
