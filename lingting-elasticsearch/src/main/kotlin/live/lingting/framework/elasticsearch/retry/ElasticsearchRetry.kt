package live.lingting.framework.elasticsearch.retry

import live.lingting.framework.elasticsearch.util.ElasticsearchUtils
import live.lingting.framework.function.ThrowingSupplier
import live.lingting.framework.retry.Retry

/**
 * @author lingting 2025/1/22 13:59
 */
class ElasticsearchRetry<T>(
    val properties: ElasticsearchRetryProperties,
    supplier: ThrowingSupplier<T>
) : Retry<T>(supplier) {

    val max = properties.maxError

    val versionConflictMax = properties.maxVersionConflict

    override fun allowRetry(ex: Throwable): Boolean {
        // 版本控制异常
        if (ElasticsearchUtils.isVersionConflictException(ex)) {
            // 无限重试
            if (versionConflictMax < 0) {
                return true
            }
            // 已重试次数 小于 设置重试次数
            return count < versionConflictMax
        }

        // 已重试次数大于等于设置重试次数
        if (count >= max) {
            return false
        }

        return true
    }

    override fun delay(ex: Throwable) {
        val delay = if (ElasticsearchUtils.isVersionConflictException(ex)) {
            properties.versionConflictDelay
        } else {
            properties.delay
        }
        Thread.sleep(delay.toMillis())
    }


}
