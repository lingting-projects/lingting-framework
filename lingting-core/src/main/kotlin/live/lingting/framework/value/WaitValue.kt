package live.lingting.framework.value

import java.time.Duration
import java.util.Objects
import java.util.concurrent.TimeoutException
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Predicate
import live.lingting.framework.lock.JavaReentrantLock
import live.lingting.framework.time.StopWatch
import live.lingting.framework.util.DurationUtils.minutes
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

    fun optional() = value.optional()

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

    @JvmOverloads
    fun notNull(duration: Duration? = null): T {
        val t = wait(duration) { obj -> Objects.nonNull(obj) }
        return t!!
    }

    @JvmOverloads
    fun notEmpty(duration: Duration? = null): T {
        val t = wait(duration) { ValueUtils.isPresent(it) }
        return t!!
    }

    @JvmOverloads
    fun wait(duration: Duration? = null, sleep: Duration = 1.minutes, predicate: Predicate<T?>): T? {
        lock.lockInterruptibly()
        try {
            val watch = StopWatch()
            watch.start()

            while (true) {
                if (predicate.test(value)) {
                    return value
                }
                val current = watch.duration()
                // 超时检测
                if (duration != null && current >= duration) {
                    throw TimeoutException("wait timeout! current: $current, timeout: $duration")
                }
                // 剩余最大休眠时间
                val sleepTime = if (duration != null) {
                    val remain = duration - current
                    if (remain > sleep) {
                        sleep
                    } else {
                        remain
                    }
                } else {
                    sleep
                }
                lock.await(sleepTime)
            }
        } finally {
            lock.unlock()
        }
    }

}
