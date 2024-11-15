package live.lingting.framework.value

import java.util.concurrent.atomic.AtomicBoolean
import live.lingting.framework.function.ThrowableSupplier
import live.lingting.framework.lock.JavaReentrantLock

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

        return lock.getByInterruptibly {
            // 首次进入时初始化
            if (first.compareAndSet(true, false)) {
                t = supplier.get()
            }
            t
        }
    }

    fun set(t: T) {
        this.t = t
        first.set(false)
    }

    fun isFirst(): Boolean {
        return first.get()
    }
}
