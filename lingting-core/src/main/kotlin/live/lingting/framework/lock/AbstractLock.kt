package live.lingting.framework.lock

import java.time.Duration
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.Condition
import live.lingting.framework.kt.milliseconds
import live.lingting.framework.time.StopWatch

/**
 * 抽象锁实现
 * @author lingting 2024/11/25 20:15
 */
abstract class AbstractLock : ExpandLock {

    companion object {

        @JvmField
        var defaultSleep = 10.milliseconds

    }

    protected open var sleep: Duration = defaultSleep

    protected open fun sleep() {
        Thread.sleep(sleep.toMillis())
    }

    override fun newCondition(): Condition {
        throw UnsupportedOperationException()
    }


    override fun lock() {
        while (true) {
            if (tryLock()) {
                return
            }
            sleep()
        }
    }

    override fun lockInterruptibly() {
        val thread = Thread.currentThread()
        while (true) {
            if (tryLock()) {
                return
            }
            if (thread.isInterrupted) {
                throw InterruptedException()
            }
            sleep()
        }
    }

    override fun tryLock(time: Long, unit: TimeUnit): Boolean {
        val millis = unit.toMillis(time)
        val thread = Thread.currentThread()
        val watch = StopWatch()
        watch.start()
        while (true) {
            if (tryLock()) {
                return true
            }
            if (watch.timeMillis() >= millis) {
                return false
            }
            if (thread.isInterrupted) {
                throw InterruptedException()
            }
            sleep()
        }
    }

}
