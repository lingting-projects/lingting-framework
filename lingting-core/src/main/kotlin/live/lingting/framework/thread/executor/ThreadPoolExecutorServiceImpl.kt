package live.lingting.framework.thread.executor

import java.util.concurrent.ThreadPoolExecutor
import live.lingting.framework.thread.ThreadService

/**
 * @author lingting 2024/12/19 23:35
 */
open class ThreadPoolExecutorServiceImpl(protected var executor: ThreadPoolExecutor) : ThreadService {

    override fun executor() = executor

    open fun executor(executor: ThreadPoolExecutor): ThreadPoolExecutorServiceImpl {
        this.executor = executor
        return this
    }

    val corePoolSize: Long
        /**
         * 核心线程数
         */
        get() = executor.corePoolSize.toLong()

    val activeCount: Long
        /**
         * 活跃线程数
         */
        get() = executor.activeCount.toLong()

    val taskCount: Long
        /**
         * 已执行任务总数
         */
        get() = executor.taskCount

    val maximumPoolSize: Long
        /**
         * 允许的最大线程数量
         */
        get() = executor.maximumPoolSize.toLong()

    val isReject: Boolean
        /**
         * 是否可能触发拒绝策略, 仅为估算
         */
        get() {
            val activeCount = activeCount
            val size = maximumPoolSize

            // 活跃线程占比未达到 90% 不可能
            val per = activeCount / size
            if (per <= 90) {
                return false
            }

            // 占比达到90%的情况下, 剩余可用线程数小于10 则可能触发拒绝
            return size - activeCount < 10
        }
}
