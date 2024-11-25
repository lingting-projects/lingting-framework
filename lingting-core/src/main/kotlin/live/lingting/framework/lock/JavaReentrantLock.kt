package live.lingting.framework.lock

import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.ReentrantLock

/**
 * @author lingting 2023-04-22 10:55
 */
class JavaReentrantLock : LocalLock {

    /**
     * 锁
     */
    val lock: ReentrantLock = ReentrantLock()

    /**
     * 激活与休眠线程
     */
    val defaultCondition: Condition = lock.newCondition()

    override fun lock() {
        lock.lock()
    }

    override fun lockInterruptibly() {
        lock.lockInterruptibly()
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

    override fun defaultCondition(): Condition {
        return defaultCondition
    }

}
