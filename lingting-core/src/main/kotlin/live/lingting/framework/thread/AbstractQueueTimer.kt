package live.lingting.framework.thread

import java.time.Duration
import java.util.PriorityQueue
import java.util.concurrent.TimeUnit
import live.lingting.framework.lock.JavaReentrantLock

/**
 * @author lingting 2023-04-22 10:39
 */
abstract class AbstractQueueTimer<T> : AbstractThreadApplicationComponent() {
    protected val lock: JavaReentrantLock = JavaReentrantLock()

    protected val queue: PriorityQueue<T> = PriorityQueue(comparator)

    abstract val comparator: Comparator<T>

    /**
     * 还有多久要处理该对象
     * @param t 对象
     * @return 具体处理该对象还要多久, 单位: 毫秒
     */
    protected abstract fun sleepTime(t: T): Duration

    fun put(t: T) {
        try {
            lock.runByInterruptibly {
                queue.add(t)
                lock.signalAll()
            }
        } catch (_: InterruptedException) {
            interrupt()
        } catch (e: Exception) {
            log.error("{} put error, param: {}", simpleName, t, e)
        }
    }

    /**
     * 将取出的元素重新放入队列
     */
    fun replay(t: T) {
        put(t)
    }

    override fun doRun() {
        val t: T? = pool()
        lock.runByInterruptibly {
            if (t == null) {
                lock.await(1, TimeUnit.HOURS)
                return@runByInterruptibly
            }
            val duration = sleepTime(t)
            val millis = duration.toMillis()
            if (
            // 需要休眠
                millis > 0
                // 如果是被唤醒
                && lock.await(millis, TimeUnit.MILLISECONDS)
            ) {
                replay(t)
                return@runByInterruptibly
            }
            process(t)
        }
    }

    protected fun pool(): T {
        return queue.poll()
    }

    protected abstract fun process(t: T)

    override fun onInterrupt() {
        log.warn("Class: {}; ThreadId: {}; interrupt! unprocessed data size: {}", simpleName, threadId(), queue.size)
    }

}
