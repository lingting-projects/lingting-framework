package live.lingting.framework.thread

import live.lingting.framework.lock.JavaReentrantLock
import java.time.Duration
import java.util.concurrent.TimeUnit

/**
 * @author lingting 2022/6/27 20:26
 */
abstract class AbstractTimer : AbstractThreadContextComponent() {
    protected val lock: JavaReentrantLock = JavaReentrantLock()

    open val timeout: Duration
        /**
         * 获取超时时间, 单位: 毫秒
         */
        get() = Duration.ofSeconds(30)

    /**
     * 执行任务
     */

    protected abstract fun process()


    override fun doRun() {
        lock.lockInterruptibly()
        try {
            process()
        } finally {
            val duration = timeout
            lock.await(duration.toMillis(), TimeUnit.MILLISECONDS)
            lock.unlock()
        }
    }

    /**
     * 唤醒定时器, 立即执行代码
     */

    fun wake() {
        lock.runByTry { lock.signalAll() }
    }
}
