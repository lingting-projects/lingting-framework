package live.lingting.framework.elasticsearch.retry

import java.time.Duration
import live.lingting.framework.elasticsearch.ElasticsearchProperties
import live.lingting.framework.util.DurationUtils.millis

/**
 * @author lingting 2025/1/22 13:59
 */
class ElasticsearchRetryProperties {

    companion object {

        @JvmStatic
        fun from(properties: ElasticsearchProperties?): ElasticsearchRetryProperties? {
            if (properties == null || !properties.retry.enable) {
                return null
            }
            return ElasticsearchRetryProperties().also {
                val retry = properties.retry
                it.onError = true
                it.maxError = retry.maxRetry
                it.onVersionConflict = true
                it.maxVersionConflict = retry.versionConflictMaxRetry
                it.delay = retry.delay
                it.versionConflictDelay = retry.versionConflictDelay ?: retry.delay
            }
        }

    }


    var onError: Boolean = false

    var maxError: Int = 3

    var delay: Duration = 50.millis

    var onVersionConflict: Boolean = true

    var maxVersionConflict: Int = 30

    var versionConflictDelay: Duration = 50.millis

}
