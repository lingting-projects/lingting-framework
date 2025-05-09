package live.lingting.framework.elasticsearch

import java.time.Duration

/**
 * @author lingting 2024-03-06 16:43
 */
class ElasticsearchProperties {

    /**
     * 索引设置
     */
    var index: Index = Index()

    /**
     * 重试配置
     */
    var retry: Retry = Retry()

    /**
     * 滚动查询配置
     */
    var scroll: Scroll = Scroll()

    class Index {

        /**
         * 索引前缀
         */
        var prefix = ""

        /**
         * 索引各部分分隔符. 用于 前缀, 聚合后缀 拼接分隔. 仅在存在前/后缀时拼接
         */
        var separate = "_"

    }

    class Retry {

        var enable: Boolean = false

        /**
         * 最大重试次数
         */
        var maxRetry: Int = 3

        /**
         * 每次重试延迟
         */
        var delay: Duration = Duration.ofMillis(50)

        /**
         * 触发版本冲突时重试次数, 小于0表示无限重试
         * 此重试独立计数, 不论是否达到 [Retry.getMaxRetry] 均会按照此配置进行重试
         */
        var versionConflictMaxRetry: Int = 50

        /**
         * 版本冲突重试延迟, 未配置则按照 [Retry.getDelay] 进行
         */
        var versionConflictDelay: Duration? = null

    }

    class Scroll {
        /**
         * 滚动索引保留超时时间
         */
        var timeout: Duration = Duration.ofMinutes(15)

        /**
         * 滚动查询时每次查询数量
         */
        var size: Long = 10
    }
}
