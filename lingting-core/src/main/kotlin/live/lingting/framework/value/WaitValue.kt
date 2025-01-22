package live.lingting.framework.value

import java.util.Objects
import java.util.concurrent.TimeUnit
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Predicate
import live.lingting.framework.concurrent.Await
import live.lingting.framework.function.InterruptedRunnable
import live.lingting.framework.lock.JavaReentrantLock
import live.lingting.framework.util.OptionalUtils.optional
import live.lingting.framework.util.ValueUtils

/**
 * @author lingting 2023-05-21 20:13
 */
class WaitValue<T> {

    companion object {
        @JvmStatic
        fun <T> of(): WaitValue<T> {
            return WaitValue()
        }

        @JvmStatic
        fun <T> of(t: T?): WaitValue<T> {
            val of = of<T>()
            of.value = t
            return of
        }
    }

    val lock: JavaReentrantLock = JavaReentrantLock()

    /**
     * 如果直接调用 set 方法是不会触发 [JavaReentrantLock.signalAll]. 会导致在set之前挂起的等待线程不会被唤醒
     */
    var value: T? = null

    val isNull: Boolean
        get() = value == null

    fun update(t: T?) {
        compute { t }
    }

    /**
     * 进行运算, 同时仅允许一个线程获取
     * @param operator 运行行为
     */
    fun compute(operator: Function<T?, T?>): T? {
        return lock.getByInterruptibly {
            val v = operator.apply(value)
            value = v
            lock.signalAll()
            v
        }
    }

    fun consumer(consumer: Consumer<T?>) {
        lock.runByInterruptibly {
            consumer.accept(value)
        }
    }

    fun notNull(): T {
        val t = wait { obj -> Objects.nonNull(obj) }
        return t!!
    }

    fun notEmpty(): T {
        val t = wait { ValueUtils.isPresent(it) }
        return t!!
    }

    fun optional() = value.optional()

    fun wait(predicate: Predicate<T?>): T? {
        return wait(predicate) { lock.await(1, TimeUnit.HOURS) }
    }

    fun wait(predicate: Predicate<T?>, sleep: InterruptedRunnable): T? {
        lock.lockInterruptibly()
        try {
            return Await.wait(
                sleep = sleep,
                supplier = { value },
                predicate = { predicate.test(it) }
            )

        } finally {
            lock.unlock()
        }
    }

}
