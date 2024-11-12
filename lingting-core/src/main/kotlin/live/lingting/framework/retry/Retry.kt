package live.lingting.framework.retry

import live.lingting.framework.function.ThrowingSupplier
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration

/**
 * @author lingting 2023-10-23 19:14
 */
open class Retry<T>(protected val supplier: ThrowingSupplier<T>, protected val function: RetryFunction) {
    protected val logs: MutableList<RetryLog<T?>> = ArrayList()

    /**
     * 当前重试次数
     */
    protected var count: Int = 0


    fun get(): T? {
        return value().get()
    }


    fun value(): RetryValue<T?> {
        var ex: Exception? = null
        while (true) {
            try {
                if (ex != null) {
                    logs.add(RetryLog(null, ex))
                    val allowed = function.allowRetry(count, ex)

                    // 不允许重试
                    if (!allowed) {
                        return RetryValue(null, false, logs)
                    }

                    // 重试休眠时间获取
                    val delay = function.getDelay(count, ex)
                    // 重试计数
                    count++
                    // 休眠
                    Thread.sleep(delay!!.toMillis())
                }

                val t = supplier.get()
                logs.add(RetryLog(t, null))
                // 获取到结果
                return RetryValue(t, true, logs)
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
                ex = e
            } catch (e: Exception) {
                ex = e
            }
        }
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(Retry::class.java)
        fun <T> simple(supplier: ThrowingSupplier<T>): Retry<T> {
            return simple(3, Duration.ofMillis(10), supplier)
        }

        fun <T> simple(maxRetryCount: Int, delay: Duration?, supplier: ThrowingSupplier<T>): Retry<T> {
            return SimpleRetry(maxRetryCount, delay, supplier)
        }
    }
}
