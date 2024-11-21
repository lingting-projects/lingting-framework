package live.lingting.framework.thread

import java.util.concurrent.Callable
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy
import java.util.concurrent.TimeUnit
import java.util.function.Supplier
import live.lingting.framework.function.ThrowableRunnable

/**
 * @author lingting 2022/11/17 20:15
 */
object ThreadPool {

    @JvmField
    val instance: PoolThreadServiceImpl = PoolThreadServiceImpl(newExecutor())

    @JvmStatic
    fun newExecutor(): ThreadPoolExecutor {
        val processors = Runtime.getRuntime().availableProcessors()
        val core = processors * 50
        val max = core * 30
        // 等待任务存放队列 - 队列最大值
        val queue = max / 2
        return ThreadPoolExecutor(
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
            { runnable -> Thread(null, runnable) },
            // 拒绝策略 - 在主线程继续执行.
            CallerRunsPolicy()
        )
    }

    @JvmStatic
    fun executor(): ThreadPoolExecutor {
        return instance.executor()
    }


    @JvmStatic
    fun update(executor: ThreadPoolExecutor): PoolThreadServiceImpl {
        return instance.executor(executor)
    }

    @JvmStatic
    val isRunning: Boolean
        /**
         * 线程池是否运行中
         */
        get() = instance.isRunning

    @JvmStatic
    val corePoolSize: Long
        /**
         * 核心线程数
         */
        get() = instance.corePoolSize

    @JvmStatic
    val activeCount: Long
        /**
         * 活跃线程数
         */
        get() = instance.activeCount

    @JvmStatic
    val taskCount: Long
        /**
         * 已执行任务总数
         */
        get() = instance.taskCount

    @JvmStatic
    val maximumPoolSize: Long
        /**
         * 允许的最大线程数量
         */
        get() = instance.maximumPoolSize

    @JvmStatic
    val isReject: Boolean
        /**
         * 是否可能触发拒绝策略, 仅为估算
         */
        get() = instance.isReject


    fun execute(runnable: ThrowableRunnable) {
        instance.execute(runnable)
    }

    fun execute(name: String, runnable: ThrowableRunnable) {
        instance.execute(name, runnable)
    }

    fun execute(runnable: KeepRunnable) {
        instance.execute(runnable)
    }

    fun <T> async(supplier: Supplier<T>): CompletableFuture<T> {
        return instance.async(supplier)
    }

    fun <T> submit(callable: Callable<T>): Future<T> {
        return instance.submit(callable)
    }

}

class PoolThreadServiceImpl(protected var executor: ThreadPoolExecutor) : ThreadService {
    override fun executor(): ThreadPoolExecutor {
        return executor
    }

    fun executor(executor: ThreadPoolExecutor): PoolThreadServiceImpl {
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
