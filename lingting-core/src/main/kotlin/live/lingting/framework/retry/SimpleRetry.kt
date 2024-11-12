package live.lingting.framework.retry

import live.lingting.framework.function.ThrowingSupplier
import java.time.Duration

/**
 * @author lingting 2023-12-19 13:47
 */
class SimpleRetry<T>(
    /**
     * 最大重试次数
     */
    val maxRetryCount: Int,
    /**
     * 重试延迟
     */
    val delay: Duration?, supplier: ThrowingSupplier<T>
) : Retry<T>(supplier, SimpleRetryFunction(maxRetryCount, delay)) {
    class SimpleRetryFunction(
        /**
         * 最大重试次数
         */
        protected val maxRetryCount: Int,
        /**
         * 重试延迟
         */
        protected val delay: Duration?
    ) : RetryFunction {
        override fun allowRetry(retryCount: Int, e: Exception?): Boolean {
            return retryCount < maxRetryCount && e !is InterruptedException
        }

        override fun getDelay(retryCount: Int, e: Exception?): Duration? {
            return delay
        }
    }
}
