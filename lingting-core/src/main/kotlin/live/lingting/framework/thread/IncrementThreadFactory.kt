package live.lingting.framework.thread

import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicLong

/**
 * @author lingting 2025/6/17 17:11
 */
open class IncrementThreadFactory(
    val factory: Factory,
) : ThreadFactory {

    private val atomic = AtomicLong()

    override fun newThread(r: Runnable): Thread? {
        val id = atomic.andIncrement
        return factory.create(id, r)
    }

    fun interface Factory {

        fun create(id: Long, task: Runnable): Thread?

    }

}
