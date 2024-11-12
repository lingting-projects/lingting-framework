package live.lingting.framework.thread

import java.time.Duration
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit

/**
 * @author lingting 2024-01-03 11:25
 */
abstract class AbstractSafeBlockingQueueThread<T> : AbstractSafeQueueThread<T?>() {
    protected val queue: BlockingQueue<T> = LinkedBlockingQueue()

    override fun doPut(t: T?) {
        if (t == null) {
            return
        }
        try {
            queue.put(t)
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
            doPut(t)
        } catch (e: Exception) {
            log.error("{} put Object error, object: {}", simpleName, t, e)
            doPut(t)
        }
    }

    override fun queueSize(): Long {
        return queue.size.toLong()
    }


    override fun poll(timeout: Duration): T {
        return queue.poll(timeout.toMillis(), TimeUnit.MILLISECONDS)
    }
}
