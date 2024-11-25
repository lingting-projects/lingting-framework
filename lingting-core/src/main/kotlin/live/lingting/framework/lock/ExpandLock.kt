package live.lingting.framework.lock

import java.time.Duration
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.Lock
import java.util.function.Consumer
import java.util.function.Supplier

/**
 * @author lingting 2024/11/25 19:03
 */
data class TryResult<T>(
    val success: Boolean,
    val value: T?
) {

    fun ifSuccess(consumer: Consumer<T?>): TryResult<T> {
        if (success) {
            consumer.accept(value)
        }
        return this
    }

    fun ifFail(runnable: Runnable): TryResult<T> {
        if (!success) {
            runnable.run()
        }
        return this
    }

}

interface ExpandLock : Lock {

    fun tryLock(duration: Duration): Boolean {
        return tryLock(duration.toNanos(), TimeUnit.NANOSECONDS)
    }

    fun run(runnable: Runnable) {
        get { runnable.run() }
    }

    @Throws(InterruptedException::class)
    fun runByInterruptibly(runnable: LockRunnable) {
        getByInterruptibly { runnable.run() }
    }

    fun runByTry(runnable: LockRunnable): TryResult<Unit> {
        return getByTry { runnable.run() }
    }

    fun runByTry(timeout: Long, unit: TimeUnit, runnable: LockRunnable): TryResult<Unit> {
        return getByTry(timeout, unit) { runnable.run() }
    }

    fun runByTry(duration: Duration, runnable: LockRunnable): TryResult<Unit> {
        return getByTry(duration) { runnable.run() }
    }

    fun <R> get(runnable: Supplier<R>): R {
        lock()
        try {
            return runnable.get()
        } finally {
            unlock()
        }
    }

    @Throws(InterruptedException::class)
    fun <R> getByInterruptibly(runnable: LockSupplier<R>): R {
        lockInterruptibly()
        try {
            return runnable.get()
        } finally {
            unlock()
        }
    }

    fun <R> getByTry(runnable: LockSupplier<R>): TryResult<R> {
        if (!tryLock()) {
            return TryResult(false, null)
        }
        try {
            val r = runnable.get()
            return TryResult(true, r)
        } finally {
            unlock()
        }
    }

    fun <R> getByTry(timeout: Long, unit: TimeUnit, runnable: LockSupplier<R>): TryResult<R> {
        if (!tryLock(timeout, unit)) {
            return TryResult(false, null)
        }
        try {
            val r = runnable.get()
            return TryResult(true, r)
        } finally {
            unlock()
        }
    }

    fun <R> getByTry(duration: Duration, runnable: LockSupplier<R>): TryResult<R> {
        return getByTry(duration.toNanos(), TimeUnit.NANOSECONDS, runnable)
    }
}

interface LocalLock : ExpandLock {

    fun defaultCondition(): Condition

    fun signal() {
        defaultCondition().signal()
    }

    fun signalAll() {
        defaultCondition().signalAll()
    }

    fun await() {
        defaultCondition().await()
    }

    /**
     * @return 返回true表示在等待过程中被唤醒
     */
    fun await(timeout: Long, unit: TimeUnit): Boolean {
        return defaultCondition().await(timeout, unit)
    }

    /**
     * @return 返回true表示在等待过程中被唤醒
     */
    fun await(duration: Duration): Boolean {
        return await(duration.toNanos(), TimeUnit.NANOSECONDS)
    }

    /**
     * @return 返回值大于0表示在等待过程中被唤醒
     */
    fun awaitNanos(nanos: Long): Long {
        return defaultCondition().awaitNanos(nanos)
    }

}
