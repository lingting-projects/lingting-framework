package live.lingting.framework.thread

import live.lingting.framework.application.ApplicationComponent
import live.lingting.framework.application.ApplicationHolder
import live.lingting.framework.concurrent.Await
import live.lingting.framework.util.BooleanUtils.ifFalse
import live.lingting.framework.util.DurationUtils.millis
import live.lingting.framework.util.Slf4jUtils.logger
import live.lingting.framework.util.ThreadUtils
import live.lingting.framework.util.ThreadUtils.threadId
import live.lingting.framework.value.WaitValue
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.function.Consumer

/**
 * @author lingting 2023-04-22 10:40
 */
abstract class AbstractThreadApplication : ApplicationComponent, Runnable {

    protected open val log = logger()

    val threadValue: WaitValue<Thread> = WaitValue.of()

    var executor: ExecutorService = ThreadUtils

    fun thread(consumer: Consumer<Thread>) {
        threadValue.optional().ifPresent { consumer.accept(it) }
    }

    fun threadId(): Long {
        return threadValue.optional().map { it.threadId() }.orElse(-1L)
    }

    open fun interrupt() {
        threadValue.consumer {
            it?.isInterrupted?.ifFalse { it.interrupt() }
        }
    }

    /**
     * 安全模式, 会等待线程自动结束
     */
    var safe: Boolean = false

    open fun safe() {
        safe = true
    }

    open fun unsafe() {
        safe = false
    }

    protected open fun init() {
        //
    }

    open val isRun: Boolean
        get() {
            val available = threadValue.optional().map { !it.isInterrupted && it.isAlive }.orElse(false)
            return !ApplicationHolder.isStop && available
        }

    open fun executor(): Executor {
        return executor
    }

    abstract fun wake()

    override fun onApplicationStart() {
        threadValue.consumer {
            // 已分配, 不再重新分配
            if (it != null) {
                if (!it.isAlive) {
                    it.start()
                }
                return@consumer
            }

            val executor = executor()
            executor.execute(this)
            threadValue.wait(sleep = 100.millis) { it != null }
        }
    }

    override fun onApplicationStopBefore() {
        log.warn("Class: {}; ThreadId: {}; before interrupt!", simpleName, threadId())
        if (safe) {
            wake()
            log.debug("Class: {}; ThreadId: {}; is safe!", simpleName, threadId())
            awaitTerminated()
        }
    }

    override fun onApplicationStop() {
        log.warn("Class: {}; ThreadId: {}; interrupt!", simpleName, threadId())
        interrupt()
    }

    open val simpleName: String = javaClass.let { cls ->
        val simple = cls.getSimpleName()
        if (simple.isNullOrBlank()) {
            cls.getName()
        } else {
            simple
        }
    }

    override fun run() {
        val thread = Thread.currentThread()
        thread.name = simpleName
        threadValue.value = thread
        init()
        while (isRun) {
            try {
                doRun()
            } catch (_: InterruptedException) {
                interrupt()
                onInterrupt()
            } catch (e: Exception) {
                onError(e)
            }
        }
        val lock = threadValue.lock
        lock.run {
            threadValue.value = null
            lock.signalAll()
        }
    }

    protected abstract fun doRun()

    /**
     * 线程被中断触发.
     */
    protected open fun onInterrupt() {
        log.warn("Class: {}; ThreadId: {}; shutdown!", simpleName, threadId())
    }

    protected open fun onError(e: Exception) {
        log.error("Class: {}; ThreadId: {}; error!", simpleName, threadId(), e)
    }

    open fun awaitTerminated() {
        Await.waitTrue {
            val thread = threadValue.optional().orElse(null)
            thread == null || (!thread.isAlive || thread.isInterrupted || Thread.State.TERMINATED == thread.state)
        }
    }

}
