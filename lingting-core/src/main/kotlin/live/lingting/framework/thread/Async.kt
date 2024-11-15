package live.lingting.framework.thread

import live.lingting.framework.function.ThrowableRunnable
import live.lingting.framework.lock.JavaReentrantLock
import live.lingting.framework.util.ThreadUtils
import live.lingting.framework.util.ValueUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.concurrent.BlockingQueue
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executor
import java.util.concurrent.LinkedBlockingQueue
import java.util.function.Supplier

/**
 * @author lingting 2023-06-05 17:31
 */
class Async constructor(
    /**
     * 异步任务使用的线程池
     */
    protected val executor: Executor? = defaultExecutor,
    /**
     * 线程数量限制. -1 表示不限制
     */
    val limit: Long = UNLIMITED
) {
    protected val lock: JavaReentrantLock = JavaReentrantLock()

    /**
     * 所有异步任务列表
     */
    protected val all: MutableList<StateKeepRunnable> = CopyOnWriteArrayList()

    /**
     * 执行中任务列表
     */
    protected val running: MutableList<StateKeepRunnable> = CopyOnWriteArrayList()

    /**
     * 已完成异步任务列表
     */
    protected val completed: MutableList<StateKeepRunnable> = CopyOnWriteArrayList()

    /**
     * 待执行任务队列
     */
    protected val queue: BlockingQueue<StateKeepRunnable> = LinkedBlockingQueue()

    constructor(limit: Long) : this(defaultExecutor, limit)

    val isUnlimited: Boolean
        /**
         * 是否可以无限制使用线程
         */
        get() = limit == UNLIMITED

    fun execute(runnable: Runnable) {
        execute("", runnable)
    }

    fun execute(name: String?, runnable: Runnable) {
        submit(name) { runnable.run() }
    }

    fun submit(runnable: ThrowableRunnable) {
        submit("", runnable)
    }

    fun submit(name: String?, runnable: ThrowableRunnable) {
        val keepRunnable: StateKeepRunnable = object : StateKeepRunnable(name) {

            override fun doProcess() {
                runnable.run()
            }


            override fun onFinally() {
                super.onFinally()
                lock.runByInterruptibly {
                    completed.add(this)
                    running.remove(this)
                    walk()
                }
            }
        }

        all.add(keepRunnable)
        queue.add(keepRunnable)
        walk()
    }

    /**
     * 唤醒所有任务. 尝试执行
     */
    fun walk() {
        // 上锁确保不会多执行
        lock.runByInterruptibly {
            // 无限制 || 可以执行新任务
            if (isUnlimited || running.size < limit) {
                val runnable = queue.poll() ?: return@runByInterruptibly
                executor!!.execute(runnable)
                running.add(runnable)
            }
        }
    }

    /**
     * 等待结束
     *
     * @param duration       超时时间
     * @param forceInterrupt 是否强制中断已超时的任务
     */
    /**
     * 等待结束, 执行时间超过超时时间的任务强行中断
     *
     * @param duration 超时时间
     */

    fun await(duration: Duration? = null, forceInterrupt: Boolean = true) {
        val supplier = Supplier {
            val count = notCompletedCount()
            if (count < 1) {
                return@Supplier true
            }

            if (duration == null) {
                return@Supplier false
            }
            val millis = duration.toMillis()
            for (runnable in running) {
                // 执行时间超时
                if (runnable.time() >= millis && forceInterrupt) {
                    runnable.interrupt()
                }
            }
            false
        }
        ValueUtils.awaitTrue(supplier)
    }

    /**
     * 执行中和待执行的任务数量
     */
    fun notCompletedCount(): Long {
        return ((queue.size + running.size).toLong())
    }

    fun runningCount(): Long {
        return running.size.toLong()
    }

    /**
     * 已完成的数量
     */
    fun completedCount(): Long {
        return completed.size.toLong()
    }

    /**
     * 所有异步任务数量
     */
    fun allCount(): Long {
        return all.size.toLong()
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(Async::class.java)
        protected var defaultExecutor: Executor = VirtualThread.executor()

        const val UNLIMITED: Long = -1


        fun pool(limit: Long = UNLIMITED): Async {
            val e: Executor = ThreadUtils.executor()
            return Async(e, limit)
        }


        fun virtual(limit: Long = UNLIMITED): Async {
            val e: Executor = VirtualThread.executor()
            return Async(e, limit)
        }

        fun setDefaultExecutor(defaultExecutor: Executor) {
            Companion.defaultExecutor = defaultExecutor
        }
    }
}