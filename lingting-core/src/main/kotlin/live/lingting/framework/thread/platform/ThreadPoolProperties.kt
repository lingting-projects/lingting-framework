package live.lingting.framework.thread.platform

import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.RejectedExecutionHandler
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * @author lingting 2024/12/19 23:26
 */
data class ThreadPoolProperties(
    val corePoolSize: Int,
    val maximumPoolSize: Int,
    val keepAliveTime: Long,
    val unit: TimeUnit,
    val workQueue: BlockingQueue<Runnable>,
    val threadFactory: ThreadFactory,
    val handler: RejectedExecutionHandler,
) {

    companion object {

        @JvmStatic
        fun byDefault(factory: ThreadFactory) = Runtime.getRuntime().let {
            val processors = Runtime.getRuntime().availableProcessors()
            val core = processors * 50
            val max = core * 30
            // 等待任务存放队列 - 队列最大值
            val queue = max / 2

            ThreadPoolProperties(
                // 核心线程数大小. 不论是否空闲都存在的线程
                core,
                // 最大线程数
                max,
                // 存活时间. 非核心线程数如果空闲指定时间. 就回收
                // 存活时间不宜过长. 避免任务量遇到尖峰情况时. 大量空闲线程占用资源
                15,
                // 存活时间的单位
                TimeUnit.SECONDS,
                // 这样配置. 当积压任务数量为 队列最大值 时. 会创建新线程来执行任务. 直到线程总数达到 最大线程数
                LinkedBlockingQueue(queue),
                // 新线程创建工厂 - LinkedBlockingQueue 不支持线程优先级. 所以直接新增线程就可以了
                factory,
                // 拒绝策略 - 在主线程继续执行.
                ThreadPoolExecutor.CallerRunsPolicy()
            )
        }

    }

    fun build(): ThreadPoolExecutor {
        return ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler)
    }

}
