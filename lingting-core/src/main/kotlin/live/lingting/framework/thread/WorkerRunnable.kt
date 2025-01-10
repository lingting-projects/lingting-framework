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
    protected val log = logger()

    protected val queue = LinkedBlockingQueue<Runnable>()

    /**
     * 是否完成
     */
    protected val finish = AtomicBoolean(false)

    protected val threadValue = WaitValue.of<Thread>()

    open fun push(runnable: Runnable) {
        check(!finish.get()) { "worker is finish!" }
        queue.add(runnable)
    }

    override fun run() {
        if (isFinish()) {
            log.error("worker is finish! not allowed to run again!")
            return
        }
        try {
            onStart()
            threadValue.update(Thread.currentThread())
            while (!finish.get()) {
                val thread = threadValue.value
                if (thread == null || thread.isInterrupted || !thread.isAlive) {
                    break
                }
                queue.poll(10, TimeUnit.MINUTES)?.run()
            }
        } finally {
            if (finish.compareAndSet(false, true)) {
                push { }
                onFinally()
            }
        }
    }

    fun isStarted(): Boolean = !threadValue.isNull

    fun isRunning(): Boolean = isStarted() && !isFinish()

    fun isFinish(): Boolean = finish.get()

    open fun onStart() {
        //
    }

    open fun onFinally() {
        //
    }

}
