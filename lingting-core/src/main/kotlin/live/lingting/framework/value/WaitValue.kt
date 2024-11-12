package live.lingting.framework.value

import live.lingting.framework.function.InterruptedRunnable
import live.lingting.framework.lock.JavaReentrantLock
import live.lingting.framework.util.CollectionUtils
import live.lingting.framework.util.StringUtils
import live.lingting.framework.util.ValueUtils
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.function.Predicate
import java.util.function.Supplier
import java.util.function.UnaryOperator

/**
 * @author lingting 2023-05-21 20:13
 */
class WaitValue<T> {
    protected val lock: JavaReentrantLock = JavaReentrantLock()

    /**
     * 如果直接调用 set 方法是不会触发 [JavaReentrantLock.signalAll]. 会导致在set之前挂起的等待线程不会被唤醒
     */
    protected var value: T? = null


    fun update(t: T?) {
        update { v: T? -> t }
    }


    fun update(operator: UnaryOperator<T?>) {
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

    fun compute(operator: UnaryOperator<T?>): T? {
        return lock.getByInterruptibly {
            val v = operator.apply(value)
            update(v)
            v
        }
    }


    fun notNull(): T? {
        return wait { obj: T? -> Objects.nonNull(obj) }
    }


    fun notEmpty(): T? {
        return wait { v: T? ->
            if (v == null) {
                return@wait false
            }
            if (v is Collection<*>) {
                return@wait !CollectionUtils.isEmpty(v as Collection<*>)
            } else if (v is Map<*, *>) {
                return@wait !CollectionUtils.isEmpty(v as Map<*, *>)
            } else if (v is String) {
                return@wait StringUtils.hasText(v as CharSequence)
            }
            true
        }
    }


    fun wait(predicate: Predicate<T?>?): T? {
        lock.lockInterruptibly()
        try {
            return ValueUtils.await<T?>(Supplier<T?> { value }, predicate, InterruptedRunnable { lock.await(1, TimeUnit.HOURS) })
        } finally {
            lock.unlock()
        }
    }

    val isNull: Boolean
        get() = value == null

    fun getValue(): T {
        return this.value
    }

    fun setValue(value: T) {
        this.value = value
    }

    companion object {
        fun <T> of(): WaitValue<T> {
            return WaitValue()
        }


        fun <T> of(t: T): WaitValue<T> {
            val of = of<T>()
            of.value = t
            return of
        }
    }
}
