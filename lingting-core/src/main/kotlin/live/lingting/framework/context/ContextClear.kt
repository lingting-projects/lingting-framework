package live.lingting.framework.context

import live.lingting.framework.application.ApplicationHolder
import live.lingting.framework.thread.platform.PlatformThread
import live.lingting.framework.thread.virtual.VirtualThread
import live.lingting.framework.util.Slf4jUtils.logger
import java.lang.ref.WeakReference
import java.util.concurrent.CopyOnWriteArraySet
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi

/**
 * @author lingting 2026/1/14 19:14
 */
@OptIn(ExperimentalAtomicApi::class)
object ContextClear {

    private val log = logger()

    private val started = AtomicBoolean(false)

    /**
     * 弱引用列表
     */
    private val array = CopyOnWriteArraySet<WeakReference<Context<*>>>()

    @JvmStatic
    fun start(c: Context<*>) {
        array.add(WeakReference(c))
        if (started.compareAndSet(expectedValue = false, newValue = true)) {
            async()
        }
    }

    private fun async() {
        val name = "ContextClear"
        if (VirtualThread.isRunning) {
            VirtualThread.execute(name) { clear() }
        } else if (PlatformThread.isRunning) {
            PlatformThread.execute(name) { clear() }
        } else if (!ApplicationHolder.isStop) {
            log.error("ContextClear unable start! ")
        }
    }

    private fun clear() {
        try {
            val iterator = array.iterator()
            while (iterator.hasNext()) {
                val ref = iterator.next()
                val c = ref.get()
                if (c == null) {
                    iterator.remove()
                } else {
                    c.clear()
                }

            }
        } catch (e: Exception) {
            log.error("ContextClear error! ", e)
        } finally {
            Thread.sleep(200)
            async()
        }
    }

}
