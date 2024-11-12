package live.lingting.framework.thread

import live.lingting.framework.function.ThrowableRunnable
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.Callable
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy
import java.util.concurrent.TimeUnit
import java.util.function.Supplier

/**
 * @author lingting 2022/11/17 20:15
 */
class ThreadPool private constructor() {
    init {
        throw UnsupportedOperationException("This is a utility class and cannot be instantiated")
    }

    class Impl(protected var executor: ThreadPoolExecutor) : ThreadService {
        override fun executor(): ThreadPoolExecutor {
            return executor
        }

        fun executor(executor: ThreadPoolExecutor): Impl {
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

    companion object {
        val instance: Impl = Impl(newExecutor())
        private val log: Logger = LoggerFactory.getLogger(ThreadPool::class.java)

        fun newExecutor(): ThreadPoolExecutor {
            val core = Runtime.getRuntime().availableProcessors() * 10
            val max = core * 30
            val queue = max / 2
            return ThreadPoolExecutor( // 核心线程数大小. 不论是否空闲都存在的线程
                core,  // 最大线程数
                max,  // 存活时间. 非核心线程数如果空闲指定时间. 就回收
                // 存活时间不宜过长. 避免任务量遇到尖峰情况时. 大量空闲线程占用资源
                15,  // 存活时间的单位
                TimeUnit.SECONDS,  // 等待任务存放队列 - 队列最大值
                // 这样配置. 当积压任务数量为 队列最大值 时. 会创建新线程来执行任务. 直到线程总数达到 最大线程数
                LinkedBlockingQueue(queue),  // 新线程创建工厂 - LinkedBlockingQueue 不支持线程优先级. 所以直接新增线程就可以了
                { runnable: Runnable? -> Thread(null, runnable) },  // 拒绝策略 - 在主线程继续执行.
                CallerRunsPolicy()
            )
        }

        fun instance(): Impl {
            return instance
        }

        fun executor(): ThreadPoolExecutor {
            return instance.executor()
        }


        fun update(executor: ThreadPoolExecutor): Impl {
            return instance().executor(executor)
        }

        val isRunning: Boolean
            /**
             * 线程池是否运行中
             */
            get() = instance.isRunning

        val corePoolSize: Long
            /**
             * 核心线程数
             */
            get() = instance.corePoolSize

        val activeCount: Long
            /**
             * 活跃线程数
             */
            get() = instance.activeCount

        val taskCount: Long
            /**
             * 已执行任务总数
             */
            get() = instance.taskCount

        val maximumPoolSize: Long
            /**
             * 允许的最大线程数量
             */
            get() = instance.maximumPoolSize

        val isReject: Boolean
            /**
             * 是否可能触发拒绝策略, 仅为估算
             */
            get() = instance.isReject


        fun execute(runnable: ThrowableRunnable?) {
            instance.execute(runnable!!)
        }

        fun execute(name: String?, runnable: ThrowableRunnable?) {
            instance.execute(name, runnable!!)
        }

        fun execute(runnable: KeepRunnable?) {
            instance.execute(runnable!!)
        }

        fun <T> async(supplier: Supplier<T>): CompletableFuture<T?> {
            return instance.async(supplier)
        }

        fun <T> submit(callable: Callable<T>): Future<T?> {
            return instance.submit(callable)
        }
    }
}
