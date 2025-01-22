package live.lingting.framework.function

import live.lingting.framework.util.MdcUtils
import live.lingting.framework.util.Slf4jUtils.logger
import live.lingting.framework.util.StringUtils

/**
 * 保留状态的可运行代码
 * @author lingting 2024-04-28 17:25
 */
abstract class KeepRunnable @JvmOverloads constructor(
    name: String? = null,
    mdc: Map<String, String>? = null
) : Runnable {

    protected val log = logger()

    protected var thread: Thread? = null

    var name: String = name ?: ""

    val mdc: Map<String, String> = mdc ?: MdcUtils.copyContext()

    override fun run() {
        val thread = Thread.currentThread()
        this.thread = thread
        val oldName = thread.name
        if (StringUtils.hasText(name)) {
            thread.name = name
        }

        MdcUtils.useContext(mdc) {
            try {
                process()
            } catch (_: InterruptedException) {
                interrupt()
                log.warn("Thread interrupted inside thread pool")
            } catch (throwable: Throwable) {
                log.error("Thread exception inside thread pool!", throwable)
            } finally {
                onFinally()
                thread.name = oldName
                this.thread = null
            }
        }
    }

    protected abstract fun process()

    protected open fun onFinally() {
        //
    }

    /**
     * 中断
     */
    fun interrupt() {
        thread?.also {
            if (!it.isInterrupted) {
                it.interrupt()
            }
        }
    }
}
