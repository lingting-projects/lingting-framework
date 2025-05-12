package live.lingting.framework.thread

import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicLong

/**
 * @author lingting 2025/5/9 15:28
 */
abstract class AtomicThreadFactory : ThreadFactory {

    protected val atomic = AtomicLong()

    val currentIndex
        get() = atomic.get()

    override fun newThread(r: Runnable): Thread? {
        return newThread(atomic.andIncrement, r)
    }

    abstract fun newThread(index: Long, r: Runnable): Thread?

}
