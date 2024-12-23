package live.lingting.framework.thread

import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import live.lingting.framework.util.Slf4jUtils.logger
import live.lingting.framework.value.WaitValue

/**
 * @author lingting 2024/12/23 17:25
 */
abstract class WorkerRunnable : Runnable {
    val log = logger()

    val queue = LinkedBlockingQueue<Runnable>()

    /**
     * 是否结束
     */
    val state = AtomicBoolean(true)

    val threadValue = WaitValue.of<Thread>()

    open fun push(runnable: Runnable) {
        queue.add(runnable)
    }

    override fun run() {
        try {
            if (!state.compareAndSet(true, false)) {
                return
            }
            threadValue.update(Thread.currentThread())
            while (!state.get()) {
                val thread = threadValue.value
                if (thread == null || thread.isInterrupted || !thread.isAlive) {
                    break
                }
                queue.poll(10, TimeUnit.MINUTES)?.run()
            }
        } finally {
            stop()
        }
    }

    fun isStarted(): Boolean = !threadValue.isNull

    fun isRunning(): Boolean = !state.get() && isStarted()

    fun isStopped(): Boolean = state.get()

    open fun stop() {
        state.set(false)
        push { }
    }

}
