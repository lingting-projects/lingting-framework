package live.lingting.framework.thread

import live.lingting.framework.context.ContextComponent
import live.lingting.framework.context.ContextHolder
import live.lingting.framework.util.StringUtils
import live.lingting.framework.util.ThreadUtils
import live.lingting.framework.util.ValueUtils
import live.lingting.framework.value.WaitValue
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Supplier

/**
 * @author lingting 2023-04-22 10:40
 */
abstract class AbstractThreadContextComponent : ContextComponent {
    protected val log: Logger = LoggerFactory.getLogger(javaClass)


    val threadValue: WaitValue<Thread> = WaitValue.of<Thread>()

    private var executor: ExecutorService? = VirtualThread.executor()

    protected fun thread(consumer: Consumer<Thread?>) {
        thread<Any?>({ thread: Thread? ->
            consumer.accept(thread)
            null
        }, null)
    }

    protected fun <T> thread(function: Function<Thread?, T>, defaultValue: T): T {
        if (!threadValue.isNull) {
            val value = threadValue.value
            return function.apply(value)
        }
        return defaultValue
    }

    protected fun threadId(): Long {
        return thread({ obj: Thread? -> obj!!.threadId() }, -1L)
    }


    protected fun interrupt() {
        thread { t: Thread? ->
            if (!t!!.isInterrupted) {
                t.interrupt()
            }
        }
        threadValue.update(null as Thread?)
    }

    protected fun init() {
    }

    open val isRun: Boolean
        get() {
            val threadAvailable = thread({ thread: Thread? -> !thread!!.isInterrupted && thread.isAlive }, false)
            return threadAvailable && !ContextHolder.isStop()
        }

    protected open fun executor(): Executor? {
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
        val current = executor()
        current!!.execute(object : KeepRunnable(name) {

            override fun process() {
                val thread = Thread.currentThread()
                threadValue.update(thread)
                try {
                    this@AbstractThreadContextComponent.run()
                } finally {
                    threadValue.update(null as Thread?)
                }
            }
        })
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
            } catch (e: InterruptedException) {
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

    protected fun error(e: Exception?) {
        log.error("Class: {}; ThreadId: {}; error!", simpleName, threadId(), e)
    }

    fun awaitTerminated() {
        log.debug("wait thread terminated.")
        ValueUtils.awaitTrue(Supplier<Boolean?> { thread<Boolean>({ thread: Thread? -> Thread.State.TERMINATED == thread!!.state }, true) })
        log.debug("thread terminated.")
    }
}
