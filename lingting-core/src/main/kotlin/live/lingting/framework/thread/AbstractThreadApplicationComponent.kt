package live.lingting.framework.thread

import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.function.Consumer
import live.lingting.framework.application.ApplicationComponent
import live.lingting.framework.application.ApplicationHolder
import live.lingting.framework.concurrent.Await
import live.lingting.framework.function.KeepRunnable
import live.lingting.framework.util.BooleanUtils.ifFalse
import live.lingting.framework.util.DurationUtils.millis
import live.lingting.framework.util.Slf4jUtils.logger
import live.lingting.framework.util.StringUtils
import live.lingting.framework.util.ThreadUtils
import live.lingting.framework.value.WaitValue

/**
 * @author lingting 2023-04-22 10:40
 */
abstract class AbstractThreadApplicationComponent : ApplicationComponent {

    protected val log = logger()

    val threadValue: WaitValue<Thread> = WaitValue.of<Thread>()

    private var executor: ExecutorService = ThreadUtils.executor()

    fun thread(consumer: Consumer<Thread>) {
        threadValue.optional().ifPresent { consumer.accept(it) }
    }

    fun threadId(): Long {
        return threadValue.optional().map { it.threadId() }.orElse(-1L)
    }

    fun interrupt() {
        threadValue.consumer {
            it?.isInterrupted?.ifFalse { it.interrupt() }
        }
    }

    /**
     * 安全模式, 会等待线程自动结束
     */
    var safe: Boolean = false

    fun safe() {
        safe = true
    }

    fun unsafe() {
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

    fun useThreadPool() {
        executor = ThreadUtils.executor()
    }

    fun useThreadVirtual() {
        executor = VirtualThread.executor()
    }

    abstract fun wake();

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
            val name = simpleName
            val runnable = object : KeepRunnable(name) {
                override fun process() {
                    val thread = Thread.currentThread()
                    threadValue.value = thread
                    try {
                        this@AbstractThreadApplicationComponent.run()
                    } finally {
                        threadValue.update(null)
                    }
                }
            }
            executor.execute(runnable)
            threadValue.wait({ it != null }) {
                threadValue.lock.await(100.millis)
            }
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

    val simpleName: String
        get() {
            val simpleName: String = javaClass.getSimpleName()
            if (StringUtils.hasText(simpleName)) {
                return simpleName
            }
            return javaClass.getName()
        }

    fun run() {
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
    }

    protected abstract fun doRun()

    /**
     * 线程被中断触发.
     */
    protected open fun onInterrupt() {
        log.warn("Class: {}; ThreadId: {}; shutdown!", simpleName, threadId())
    }

    protected fun onError(e: Exception) {
        log.error("Class: {}; ThreadId: {}; error!", simpleName, threadId(), e)
    }

    fun awaitTerminated() {
        Await.waitTrue { threadValue.optional().map { Thread.State.TERMINATED == it.state }.orElse(true) }
    }

}
