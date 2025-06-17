package live.lingting.framework.retry

import java.time.Duration
import live.lingting.framework.function.ThrowingSupplier

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
    val delay: Duration,
    supplier: ThrowingSupplier<T>
) : Retry<T>(supplier) {

    private val sleep = delay.toMillis()

    override fun allowRetry(ex: Throwable): Boolean {
        return ex !is InterruptedException && count < maxRetryCount
    }

    override fun delay(ex: Throwable) {
        Thread.sleep(sleep)
    }

}
