package live.lingting.framework.queue

import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit

/**
 * 等待队列
 *
 * @author lingting 2023/1/29 10:52
 */
class WaitQueue<V> constructor(private val queue: LinkedBlockingQueue<V> = LinkedBlockingQueue()) {
    fun get(): V {
        return queue.poll()
    }


    fun poll(timeout: Long = 10, unit: TimeUnit? = TimeUnit.HOURS): V {
        var v: V?
        do {
            v = queue.poll(timeout, unit)
        } while (v == null)
        return v
    }

    fun clear() {
        queue.clear()
    }

    fun add(seat: V) {
        queue.add(seat)
    }

    fun addAll(accounts: Collection<V>) {
        for (account in accounts) {
            add(account)
        }
    }
}
