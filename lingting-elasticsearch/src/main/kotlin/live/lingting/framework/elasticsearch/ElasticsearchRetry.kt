package live.lingting.framework.elasticsearch

import java.time.Duration
import live.lingting.framework.function.ThrowingSupplier
import live.lingting.framework.retry.Retry
import live.lingting.framework.retry.RetryFunction

/**
 * @author lingting 2023-12-19 14:19
 */
class ElasticsearchRetry<T>(
    retry: ElasticsearchProperties.Retry,
    supplier: ThrowingSupplier<T>
) : Retry<T>(
    supplier,
    ElasticsearchRetryFunction(retry)
) {
    class ElasticsearchRetryFunction(val retry: ElasticsearchProperties.Retry) : RetryFunction {
        var versionConflictCount: Int = 0
            protected set

        var count: Int = 0
            protected set

        override fun allowRetry(retryCount: Int, e: Throwable): Boolean {
            if (!retry.isEnabled) {
                return false
            }

            // 版本控制异常
            if (ElasticsearchUtils.isVersionConflictException(e)) {
                return allowVersionConflictRetry()
            }

            // 已重试次数大于等于设置重试次数
            if (retryCount >= retry.maxRetry) {
                return false
            }

            // 计数
            count++
            return true
        }

        protected fun allowVersionConflictRetry(): Boolean {
            // 非无限重试时,已重试次数大于等于设置重试次数
            if (retry.versionConflictMaxRetry > 0 && versionConflictCount >= retry.versionConflictMaxRetry) {
                return false
            }

            // 允许重试, 计数
            versionConflictCount++
            return true
        }

        override fun getDelay(retryCount: Int, e: Throwable): Duration {
            if (ElasticsearchUtils.isVersionConflictException(e) && retry.versionConflictDelay != null) {
                return retry.versionConflictDelay!!
            }
            return retry.delay
        }
    }
}
