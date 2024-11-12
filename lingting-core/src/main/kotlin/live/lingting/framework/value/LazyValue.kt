package live.lingting.framework.value

import live.lingting.framework.function.ThrowableSupplier
import live.lingting.framework.lock.JavaReentrantLock
import live.lingting.framework.lock.LockSupplier
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @author lingting 2024-09-28 15:29
 */
class LazyValue<T>(protected val supplier: ThrowableSupplier<T>) {
    protected val lock: JavaReentrantLock = JavaReentrantLock()

    protected val first: AtomicBoolean = AtomicBoolean(true)


    var t: T? = null


    fun get(): T? {
        if (!isFirst()) {
            return t
        }

        t = lock.getByInterruptibly(LockSupplier { // 非首次进入锁
            if (!first.compareAndSet(true, false)) {
                return@LockSupplier t
            }
            // 首次进入时初始化
            supplier.get()
        })
        return t
    }

    fun set(t: T) {
        this.t = t
        first.set(false)
    }

    fun isFirst(): Boolean {
        return first.get()
    }
}
