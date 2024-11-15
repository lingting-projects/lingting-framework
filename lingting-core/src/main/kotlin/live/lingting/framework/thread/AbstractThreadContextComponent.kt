package live.lingting.framework.thread

import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.function.Consumer
import live.lingting.framework.context.ContextComponent
import live.lingting.framework.context.ContextHolder
import live.lingting.framework.kt.ifTrue
import live.lingting.framework.kt.logger
import live.lingting.framework.util.StringUtils
import live.lingting.framework.util.ThreadUtils
import live.lingting.framework.util.ValueUtils
import live.lingting.framework.value.WaitValue

/**
 * @author lingting 2023-04-22 10:40
 */
abstract class AbstractThreadContextComponent : ContextComponent {
    protected val log = logger()

    val threadValue: WaitValue<Thread> = WaitValue.of<Thread>()

    private var executor: ExecutorService = VirtualThread.executor()

    protected fun thread(consumer: Consumer<Thread>) {
        threadValue.optional().ifPresent { consumer.accept(it) }
    }

    protected fun threadId(): Long {
        return threadValue.optional().map { it.threadId() }.orElse(-1L)
    }

    protected fun interrupt() {
        threadValue.consumer {
            it?.isInterrupted.ifTrue { it?.interrupt() }
        }
    }

    protected fun init() {
    }

    open val isRun: Boolean
        get() {
            val available = threadValue.optional().map { !it.isInterrupted && it.isAlive }.orElse(false)
            return !ContextHolder.isStop && available
        }

    protected open fun executor(): Executor {
        return executor
    }

    fun useThreadPool() {
        executor = ThreadUtils.executor()
    }

    fun useThreadVirtual() {
        executor = VirtualThread.executor()
    }

    override fun onApplicationStart() {
        val name = simpleName
        threadValue.consumer {
            // 已分配, 不再重新分配
            if (it != null) {
                return@consumer
            }

            executor().execute(object : KeepRunnable(name) {
                override fun process() {
                    val thread = Thread.currentThread()
                    threadValue.update(thread)
                    try {
                        this@AbstractThreadContextComponent.run()
                    } finally {
                        threadValue.update(null)
                    }
                }
            })
        }
    }

    override fun onApplicationStop() {
        log.warn("Class: {}; ThreadId: {}; closing!", simpleName, threadId())
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
                shutdown()
            } catch (e: Exception) {
                error(e)
            }
        }
    }


    protected abstract fun doRun()

    /**
     * 线程被中断触发.
     */
    protected open fun shutdown() {
        log.warn("Class: {}; ThreadId: {}; shutdown!", simpleName, threadId())
    }

    protected fun error(e: Exception) {
        log.error("Class: {}; ThreadId: {}; error!", simpleName, threadId(), e)
    }

    fun awaitTerminated() {
        log.debug("wait thread terminated.")
        ValueUtils.awaitTrue { threadValue.optional().map { Thread.State.TERMINATED == it.state }.orElse(true) }
        log.debug("thread terminated.")
    }
}
