package live.lingting.framework.retry

import java.time.Duration
import live.lingting.framework.function.ThrowingSupplier

/**
 * @author lingting 2023-10-23 19:14
 */
open class Retry<T>(
    protected val supplier: ThrowingSupplier<T>,
    protected val function: RetryFunction
) {

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

    val logs by lazy { run() }

    protected open fun run(): List<RetryLog<T>> {
        return mutableListOf<RetryLog<T>>().apply {
            var ex: Throwable? = null
            while (true) {
                try {
                    if (ex != null) {
                        add(RetryLog(null, ex))
                        val allowed = function.allowRetry(count, ex)

                        // 不允许重试
                        if (!allowed) {
                            break
                        }

                        // 重试休眠时间获取
                        val delay = function.getDelay(count, ex)
                        // 重试计数
                        count++
                        // 休眠
                        Thread.sleep(delay!!.toMillis())
                    }

                    val t = supplier.get()
                    add(RetryLog(t, null))
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

