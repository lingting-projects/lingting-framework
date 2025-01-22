package live.lingting.framework.retry

import java.time.Duration
import live.lingting.framework.function.ThrowingSupplier

/**
 * @author lingting 2023-10-23 19:14
 */
abstract class Retry<T>(protected val supplier: ThrowingSupplier<T>) {

    companion object {
        @JvmStatic
        fun <T> simple(supplier: ThrowingSupplier<T>): Retry<T> {
            return simple(3, Duration.ofMillis(10), supplier)
        }

        @JvmStatic
        fun <T> simple(maxRetryCount: Int, delay: Duration, supplier: ThrowingSupplier<T>): Retry<T> {
            return SimpleRetry(maxRetryCount, delay, supplier)
        }
    }

    /**
     * 当前重试次数
     */
    protected var count: Int = 0

    val logs by lazy { buildLogs() }

    protected open fun log(t: T?, ex: Throwable?) = RetryLog(t, ex)

    protected abstract fun allowRetry(ex: Throwable): Boolean

    protected abstract fun delay(ex: Throwable)

    protected open fun buildLogs(): List<RetryLog<T>> {
        return mutableListOf<RetryLog<T>>().apply {
            var ex: Throwable? = null
            while (true) {
                try {
                    if (ex != null) {
                        val el = log(null, ex)
                        add(el)

                        // 不允许重试
                        if (!allowRetry(ex)) {
                            break
                        }

                        // 休眠
                        delay(ex)
                        // 重试计数
                        count++
                    }

                    val t = supplier.get()
                    val tl = log(t, null)
                    add(tl)
                    break
                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt()
                    ex = e
                } catch (e: Throwable) {
                    ex = e
                }
            }
        }.toList()
    }

    fun last() = logs.last()

    fun get(): T? {
        val (value, ex) = last()
        if (ex != null) {
            throw ex
        }
        return value
    }

    fun nonNull(): T {
        val t = get()
        checkNotNull(t) { "Retry value must not be null!" }
        return t
    }

}

