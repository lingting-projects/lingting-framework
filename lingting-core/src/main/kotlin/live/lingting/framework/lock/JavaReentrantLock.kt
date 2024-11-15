package live.lingting.framework.lock

import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.ReentrantLock
import java.util.function.Supplier

/**
 * @author lingting 2023-04-22 10:55
 */
class JavaReentrantLock {
    /**
     * 锁
     */
    val lock: ReentrantLock = ReentrantLock()

    /**
     * 激活与休眠线程
     */
    val defaultCondition: Condition = lock.newCondition()

    fun newCondition(): Condition {
        return lock.newCondition()
    }

    fun lock() {
        lock.lock()
    }


    fun lockInterruptibly() {
        lock.lockInterruptibly()
    }


    fun lockTry(timeout: Long = 1, unit: TimeUnit = TimeUnit.MILLISECONDS): Boolean {
        return lock.tryLock(timeout, unit)
    }

    fun run(runnable: Runnable) {
        lock()
        try {
            runnable.run()
        } finally {
            unlock()
        }
    }


    fun runByInterruptibly(runnable: LockRunnable) {
        lockInterruptibly()
        try {
            runnable.run()
        } finally {
            unlock()
        }
    }


    fun runByTry(runnable: LockRunnable) {
        if (lockTry()) {
            try {
                runnable.run()
            } finally {
                unlock()
            }
        }
    }


    fun runByTry(runnable: LockRunnable, timeout: Long, unit: TimeUnit) {
        if (lockTry(timeout, unit)) {
            try {
                runnable.run()
            } finally {
                unlock()
            }
        }
    }

    fun <R> get(runnable: Supplier<R>): R {
        val reentrantLock = lock
        reentrantLock.lock()
        try {
            return runnable.get()
        } finally {
            reentrantLock.unlock()
        }
    }


    fun <R> getByInterruptibly(runnable: () -> R): R {
        val reentrantLock = lock
        reentrantLock.lockInterruptibly()
        try {
            return runnable()
        } finally {
            reentrantLock.unlock()
        }
    }

    fun unlock() {
        lock.unlock()
    }


    fun signal() {
        runByInterruptibly { defaultCondition.signal() }
    }


    fun signalAll() {
        runByInterruptibly { defaultCondition.signalAll() }
    }


    fun await() {
        runByInterruptibly { defaultCondition.await() }
    }

    /**
     * @return 是否被唤醒
     */
    fun await(time: Long, timeUnit: TimeUnit): Boolean {
        return getByInterruptibly { defaultCondition.await(time, timeUnit) }
    }
}
