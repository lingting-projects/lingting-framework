package live.lingting.framework.thread

import java.time.Duration
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit

/**
 * 抽象的线程类，主要用于汇聚详情数据 做一些基础的处理后 进行批量插入
 * @author lingting
 */
abstract class AbstractBlockingQueueThread<T> : AbstractQueueThread<T>() {
    protected val queue: BlockingQueue<T> = LinkedBlockingQueue()

    override val queueSize = queue.size

    override fun put(t: T) {
        try {
            if (t != null) {
                queue.put(t)
            }
        } catch (_: InterruptedException) {
            Thread.currentThread().interrupt()
        } catch (e: Exception) {
            log.error("{} put Object error, object: {}", simpleName, t, e)
        }
    }

    override fun poll(timeout: Duration): T? {
        return queue.poll(timeout.toMillis(), TimeUnit.MILLISECONDS)
    }
}
