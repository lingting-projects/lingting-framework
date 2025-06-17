package live.lingting.framework.thread

import live.lingting.framework.util.Slf4jUtils.logger
import live.lingting.framework.value.WaitValue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit

/**
 * @author lingting 2024/12/23 17:25
 */
abstract class WorkerRunnable : Runnable {

    protected val log = logger()

    protected val queue = LinkedBlockingQueue<Runnable>()

    protected val threadValue = WaitValue.of<Thread>()

    protected val lock = threadValue.lock

    protected var finish = false

    open fun push(runnable: Runnable) {
        check(!isFinish()) { "worker is finish!" }
        queue.add(runnable)
    }

    override fun run() {
        if (isFinish()) {
            log.error("worker is finish! not allowed to run again!")
            return
        }
        try {
            onStart()
            val thread = Thread.currentThread()
            threadValue.update(thread)
            while (!isFinish()) {
                val poll = queue.poll(10, TimeUnit.MINUTES)
                poll?.run()
            }
        } finally {
            interrupt()
        }
    }

    fun isStarted(): Boolean = !threadValue.isNull

    fun isRunning(): Boolean = isStarted() && !isFinish()

    fun isFinish(): Boolean = finish && threadValue.value.let { it == null || it.isInterrupted || !it.isAlive }

    fun interrupt() {
        threadValue.compute {
            if (!finish) {
                finish = true
                queue.add { }
                onFinally()
            }
            null
        }
    }

    protected open fun onStart() {
        //
    }

    protected open fun onFinally() {
        //
    }

}
