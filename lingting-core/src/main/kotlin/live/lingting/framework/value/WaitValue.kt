package live.lingting.framework.value

import live.lingting.framework.lock.JavaReentrantLock
import live.lingting.framework.util.ValueUtils

import java.util.concurrent.TimeUnit
import java.util.function.Function
import java.util.function.Predicate

/**
 * @author lingting 2023-05-21 20:13
 */
class WaitValue<T> {
    protected val lock: JavaReentrantLock = JavaReentrantLock()

    /**
     * 如果直接调用 set 方法是不会触发 [JavaReentrantLock.signalAll]. 会导致在set之前挂起的等待线程不会被唤醒
     */
    var value: T? = null

    val isNull: Boolean
        get() = value == null

    fun update(t: T) {
        update { t }
    }


    fun update(operator: Function<T?, T>) {
        lock.runByInterruptibly {
            value = operator.apply(value)
            lock.signalAll()
        }
    }

    /**
     * 进行运算, 同时仅允许一个线程获取
     *
     * @param operator 运行行为
     */

    fun compute(operator: Function<T?, T>): T {
        return lock.getByInterruptibly {
            val v = operator.apply(value)
            update(v)
            v
        }
    }


    fun notNull(): T {
        return wait { obj: T -> Objects.nonNull(obj) }
    }


    fun notEmpty(): T {
        return wait { ValueUtils.isPresent(it) }
    }


    fun wait(predicate: Predicate<T>): T {
        lock.lockInterruptibly()
        try {
            return ValueUtils.await(
                { value },
                { it != null && predicate.test(it) },
                { lock.await(1, TimeUnit.HOURS) }
            )!!
        } finally {
            lock.unlock()
        }
    }


    companion object {
        @JvmStatic
        fun <T> of(): WaitValue<T> {
            return WaitValue()
        }


        @JvmStatic
        fun <T> of(t: T): WaitValue<T> {
            val of = of<T>()
            of.value = t
            return of
        }
    }
}
