package live.lingting.framework.thread

import java.time.Duration
import live.lingting.framework.lock.JavaReentrantLock

/**
 * @author lingting 2022/6/27 20:26
 */
abstract class AbstractTimer : AbstractThreadApplication() {

    protected val lock: JavaReentrantLock = JavaReentrantLock()

    open val interval: Duration = Duration.ofSeconds(30)

    /**
     * 执行任务
     */
    protected abstract fun process()

    override fun doRun() {
        lock.lockInterruptibly()
        try {
            process()
        } finally {
            lock.await(interval)
            lock.unlock()
        }
    }

    /**
     * 唤醒定时器, 立即执行代码
     */
    override fun wake() {
        lock.runByTry { lock.signalAll() }
    }

}
