package live.lingting.framework.lock

import java.time.Duration
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.Lock
import live.lingting.framework.kt.milliseconds

/**
 * @author lingting 2024/11/25 19:26
 */
class SpinLock @JvmOverloads constructor(
    val lock: Lock,
    val sleep: Duration = 300.milliseconds,
) : ExpandLock {

    companion object {
        @JvmStatic
        @JvmOverloads
        fun java(sleep: Duration = 300.milliseconds) = SpinLock(JavaReentrantLock(), sleep)
    }

    override fun lock() {
        while (true) {
            if (lock.tryLock()) {
                return
            }
            Thread.sleep(sleep.toMillis())
        }
    }

    override fun lockInterruptibly() {
        while (true) {
            if (lock.tryLock(sleep.toMillis(), TimeUnit.MILLISECONDS)) {
                return
            }
        }
    }

    override fun tryLock(): Boolean {
        return lock.tryLock()
    }

    override fun tryLock(time: Long, unit: TimeUnit): Boolean {
        return lock.tryLock(time, unit)
    }

    override fun unlock() {
        lock.unlock()
    }

    override fun newCondition(): Condition {
        return lock.newCondition()
    }
}
